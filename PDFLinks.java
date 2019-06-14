import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;

// read PDF files and extract link information:
// https://stackoverflow.com/questions/38587567/how-to-extract-hyperlink-information-pdfbox?rq=1

// https://github.com/apache/pdfbox/blob/trunk/pdfbox/src/main/java/org/apache/pdfbox/multipdf/Splitter.java#L234

// https://stackoverflow.com/questions/36790374/how-to-find-page-to-jump-to-i-using-pdfbox-2-0-0-and-pdactiongoto

// change PDActionGoTo destination:
// https://www.javatips.net/api/org.apache.pdfbox.pdmodel.interactive.action.type.pdactiongoto

// add a hyperlink:

// https://stackoverflow.com/questions/23553094/pdfbox-how-to-create-table-of-contents

// https://www.programcreek.com/java-api-examples/?code=ralfstuckert/pdfbox-layout/pdfbox-layout-master/src/main/java/rst/pdfbox/layout/text/annotations/HyperlinkAnnotationProcessor.java

// https://www.programcreek.com/java-api-examples/?api=org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink

public class PDFLinks {

    private static int DEBUG = 0;

    private static String fmt0 = "%7s\t%-15s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%-32s%n";
    private static String fmt = "%7s\t%-15s\t%7.3f\t%7.3f\t%7.3f\t%7.3f\t%7.3f\t%7.3f\t%7d\t%7s\t%7d\t%7d\t%7.3f\t%-32s%n";

    // the ever-present
    private static void usage(){
	System.out.printf("usage:%n");
	System.out.printf("  # list existing links in pdf file%n");
	System.out.printf("  java PDFlinks pdffile%n");
	System.out.printf("%n");
	System.out.printf("  # add links in list file to pdf file%n");
	System.out.printf("  java PDFlinks ipdffile opdffile listfile%n");
	System.out.printf("%n");
    }

    // change link style when debugging
    private static void setDebugStyle(PDAnnotationLink link, String colorStr){
	PDColor color;
	switch(colorStr){
	case "red":
	    color = new PDColor(new float[] { 1, 0, 0 }, PDDeviceRGB.INSTANCE);
	    break;
	case "green":
	    color = new PDColor(new float[] { 0, 1, 0 }, PDDeviceRGB.INSTANCE);
	    break;
	case "blue":
	    color = new PDColor(new float[] { 0, 0, 1 }, PDDeviceRGB.INSTANCE);
	    break;
	case "yellow":
	    color = new PDColor(new float[] { 1, 1, 0 }, PDDeviceRGB.INSTANCE);
	    break;
	case "magenta":
	    color = new PDColor(new float[] { 1, 0, 1}, PDDeviceRGB.INSTANCE);
	    break;
	case "cyan":
	    color = new PDColor(new float[] { 0, 1, 1 }, PDDeviceRGB.INSTANCE);
	    break;
	case "white":
	    color = new PDColor(new float[] { 0, 0, 0 }, PDDeviceRGB.INSTANCE);
	    break;
	default:
	    color = new PDColor(new float[] { 0, 0, 0 }, PDDeviceRGB.INSTANCE);
	    break;
	}
	if( DEBUG > 0 ){
	    PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
	    borderULine.setStyle(PDBorderStyleDictionary.STYLE_BEVELED);
	    borderULine.setWidth(2);
	    link.setBorderStyle(borderULine);
	    link.setColor(color);
	}
    }

    // add a URI-style link to the page
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addURILinkToPage(PDPage page, float llx, float lly, float urx, float ury, String uri) throws IOException {
        PDActionURI action = new PDActionURI();
	action.setURI(uri);

	PDAnnotationLink link = new PDAnnotationLink();
	link.setAction(action);

	PDRectangle position = new PDRectangle();
	position.setLowerLeftX(llx);
	position.setLowerLeftY(lly);
	position.setUpperRightX(urx);
	position.setUpperRightY(ury);
	link.setRectangle(position);

	// DEBUGGING
	setDebugStyle(link, "cyan");

	List annotations = page.getAnnotations();
	annotations.add(link);
    }

    // add a Named Destination link to the page
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addNamedDestToPage(PDPage page, String destination, float llx, float lly, float urx, float ury, PDPage toPage, int toLeft, int toTop) throws IOException {

	// how to we make a named destination and place it in the right location??
	// PDNamedDestination dest =  new PDNamedDestination(destination);
	PDPageXYZDestination dest = new PDPageXYZDestination();
	dest.setPage(toPage);
	dest.setLeft(toLeft);
	dest.setTop(toTop);

	PDActionGoTo action = new PDActionGoTo();
	action.setDestination(dest);

	PDAnnotationLink link = new PDAnnotationLink();
	link.setAction(action);
	// https://pdfbox.apache.org/docs/2.0.0/javadocs/org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationLink.html#setDestination(org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination)
	// says to call setDestination() on action or link, but not both
	// link.setDestination(dest);

	PDRectangle position = new PDRectangle();
	position.setLowerLeftX(llx);
	position.setLowerLeftY(lly);
	position.setUpperRightX(urx);
	position.setUpperRightY(ury);
	link.setRectangle(position);

	// DEBUGGING
	setDebugStyle(link, "red");

	List annotations = page.getAnnotations();
	annotations.add(link);
    }

    // add a Page Destination link to the page
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addPageDestToPage(PDPage page, float llx, float lly, float urx, float ury, PDPage toPage, int toLeft, int toTop) throws IOException {

	PDPageXYZDestination pageDest = new PDPageXYZDestination();
	pageDest.setPage(toPage);
	pageDest.setLeft(toLeft);
	pageDest.setTop(toTop);

	PDActionGoTo action = new PDActionGoTo();
	action.setDestination(pageDest);

	PDAnnotationLink link = new PDAnnotationLink();
	link.setAction(action);
	link.setDestination(pageDest);

	PDRectangle position = new PDRectangle();
	position.setLowerLeftX(llx);
	position.setLowerLeftY(lly);
	position.setUpperRightX(urx);
	position.setUpperRightY(ury);
	link.setRectangle(position);

	// DEBUGGING
	setDebugStyle(link, "green");

	List annotations = page.getAnnotations();
	annotations.add(link);
    }

    // addLinks: get link information from line buffer and add the link
    private static void addLinks(PDDocument doc, String tokens[]) throws IOException {
	PDPage toPage = null;
	int toLeft = 0;
	int toTop = 0;
	PDPage page = doc.getPage(Integer.parseInt(tokens[0]));
	float llx = Float.parseFloat(tokens[2]);
	float lly = Float.parseFloat(tokens[3]);
	float urx = Float.parseFloat(tokens[4]);
	float ury = Float.parseFloat(tokens[5]);
	switch(tokens[1]){
	case "uri":
	    addURILinkToPage(page, llx, lly, urx, ury, tokens[13]);
	    break;
	case "namedDest":
	    toPage = doc.getPage(Integer.parseInt(tokens[9]));
	    toLeft = Integer.parseInt(tokens[10]);
	    toTop = Integer.parseInt(tokens[11]);
	    addNamedDestToPage(page, tokens[13], llx, lly, urx, ury, toPage, toLeft, toTop);
	    break;
	case "pageDest":
	    toPage = doc.getPage(Integer.parseInt(tokens[9]));
	    toLeft = Integer.parseInt(tokens[10]);
	    toTop = Integer.parseInt(tokens[11]);
	    addPageDestToPage(page, llx, lly, urx, ury, toPage, toLeft, toTop);
	    break;
	default:
	    break;
	}
    }

    // list the links in the current page
    private static void listLinks(PDDocument doc, int i) throws IOException {
	PDDocumentCatalog catalog = doc.getDocumentCatalog();
	PDPage page = doc.getPage(i);
	int rotation = page.getRotation();
        List<PDAnnotation> annotations = page.getAnnotations();
	// the logic below is adapted from:
	// https://github.com/apache/pdfbox/blob/trunk/pdfbox/src/main/java/org/apache/pdfbox/multipdf/Splitter.java#L234
	// and:
	// https://stackoverflow.com/questions/36790374/how-to-find-page-to-jump-to-i-using-pdfbox-2-0-0-and-pdactiongoto
	// https://stackoverflow.com/questions/38587567/how-to-extract-hyperlink-information-pdfbox
	// for each annotation ...
        for( PDAnnotation annotation:annotations ){
	    // if the annotation is a link ...
            if( annotation instanceof PDAnnotationLink ){
		// get common information
                PDAnnotationLink link = (PDAnnotationLink)annotation;   
		PDRectangle rect = link.getRectangle();
		float llx = rect.getLowerLeftX();
		float lly = rect.getLowerLeftY();
		float urx = rect.getUpperRightX();
		float ury = rect.getUpperRightY();
		float width = rect.getWidth();
		float height = rect.getHeight();
		// assume no info about destination
		int pageno = -1;
		int left = 0;
		int top = 0;
		float zoom = 0.0f;
		String destStr = "";
		// no link type
		String linkType = "unknown";
		// get action associated with this link
		PDAction action = link.getAction();
		if( action instanceof PDActionURI ){
		    // URI action: target is an external link
		    destStr = ((PDActionURI)action).getURI();
		    linkType = "uri";
		} else if( action instanceof PDActionGoTo ){
		    // goto link: target is an internal link
		    PDDestination destination = link.getDestination();
		    if( destination == null ){
			destination = ((PDActionGoTo)action).getDestination();
		    }
		    if( destination instanceof PDPageDestination ){
			destStr = "";
			linkType = "pageDest";
		    } else if ( destination instanceof PDNamedDestination ){
			destStr =  ((PDNamedDestination)destination).getNamedDestination();
			PDPageDestination pageDest = catalog.findNamedDestinationPage((PDNamedDestination)destination);
			if( pageDest != null ){
			    linkType = "namedDest";
			    destination = pageDest;
			} else {
			    linkType = "namedUnknown";
			}
		    }
		    if( destination != null ){
			if( destination instanceof PDPageXYZDestination ){
			    left = ((PDPageXYZDestination)destination).getLeft();
			    top = ((PDPageXYZDestination)destination).getTop();
			    zoom = ((PDPageXYZDestination)destination).getZoom();
			}
			pageno = ((PDPageDestination) destination).retrievePageNumber();
		    }
		}
		// output the link
		System.out.printf(fmt, i, linkType,
				  llx, lly, urx, ury, width, height, rotation,
				  pageno, left, top, zoom, destStr);
	    }
	}
    }

    // int main(int argv, char **argv){ would be easier ...
    public static void main (String argv[]) throws IOException {
       PDDocument doc = null;
       File f = null;
       BufferedReader b = null;
       String lbuf = "";
       String tokens[];
       String s = System.getenv("PDFLINKS_DEBUG");
       int lineno = 0;
       // user specified debugging
       if( s != null ){
	   DEBUG = Integer.parseInt(s);
       }
       // check arg count
       if( argv.length == 0 ){
	   usage();
	   System.exit(1);
       }
       if( argv.length == 1 ){
	   // 1 arg: display links
	   doc = PDDocument.load(new File(argv[0]));
	   // output header
	   System.out.printf(fmt0, "onPage", "linkType", "rectLLX", "rectLLY", "rectURX", "rectURY", "rectWid", "rectHt", "rectRot", "toPage", "toLeft", "toTop", "toZoom", "namedDestOrURI");
	   System.out.printf(fmt0, "------", "---------------", "-------", "-------", "-------", "-------", "-------", "-------", "-------", "------", "------", "-----", "------", "--------------");
	   // list links in all pages
	   for(int i = 0; i < doc.getNumberOfPages(); i++){
	       listLinks(doc, i);
	   }
	   doc.close();
       } else if( argv.length == 3 ){
	   // 3 args: add links
	   doc = PDDocument.load(new File(argv[0]));
	   // 3rd arg: text file containing list of links
	   f = new File(argv[2]);
	   b = new BufferedReader(new FileReader(f));
	   // for each link (line in the link file)
	   while( (lbuf = b.readLine()) != null ){
	       lineno++;
	       // split each line into trimmed tokens, skipping the rdb header
	       if( lineno <= 2 ){ continue; }
	       tokens = lbuf.split("\t");
	       for(int i=0; i<tokens.length; i++){
		   tokens[i] = tokens[i].trim();
	       }
	       // add back this link
	       addLinks(doc, tokens);
	   }
	   // save changes to output file
	   doc.save(argv[1]);
	   doc.close();
       } else {
	   // wrong number of args
	   usage();
	   System.exit(1);
       }
   }  
}
