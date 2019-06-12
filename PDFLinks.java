import java.io.File;
import java.io.IOException; 
import java.util.List;
import java.awt.geom.Rectangle2D;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
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

// wtf am I doing in Java???
public class PDFLinks {

    private static String fmt0 = "%7s\t%-15s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%-32s%n";
    private static String fmt = "%7s\t%-15s\t%7.3f\t%7.3f\t%7.3f\t%7.3f\t%7.3f\t%7.3f\t%7d\t%7s\t%7d\t%7d\t%7.3f\t%-32s%n";

    private static void listPDFLinks(PDDocument doc, PDPage page, int i) throws IOException {
	PDDocumentCatalog catalog = doc.getDocumentCatalog();
        List<PDAnnotation> annotations = page.getAnnotations();
        for( PDAnnotation annotation:annotations ){
            if( annotation instanceof PDAnnotationLink ){
                PDAnnotationLink link = (PDAnnotationLink)annotation;   
		PDRectangle rect = link.getRectangle();
		float llx = rect.getLowerLeftX();
		float lly = rect.getLowerLeftY();
		float urx = rect.getUpperRightX();
		float ury = rect.getUpperRightY();
		float width = rect.getWidth();
		float height = rect.getHeight();
		int rotation = page.getRotation();
		PDAction action = link.getAction();
		if( action instanceof PDActionURI ){
		    PDActionURI uri = (PDActionURI)action;
		    System.out.printf(fmt, i, "uri", llx, lly, urx, ury, width, height, rotation, "N/A", 0, 0, 0.0, uri.getURI());
		} else if( action instanceof PDActionGoTo ){
		    PDDestination destination = link.getDestination();
		    if( destination == null && link.getAction() != null ){
			if( action instanceof PDActionGoTo ){
			    destination = ((PDActionGoTo)action).getDestination();
			}
		    }
		    PDPageDestination pageDestination = null;
		    if( destination instanceof PDPageDestination ){
			pageDestination = (PDPageDestination) destination;
			// page = pageDestination.getPage();
			int ppage = pageDestination.retrievePageNumber();
			System.out.printf(fmt, i, "pageDest", llx, lly, urx, ury, width, height, rotation, ppage, 0, 0, 0.0, "");
		    } else if ( destination instanceof PDNamedDestination ){
			PDNamedDestination namedDestination = (PDNamedDestination)destination;
			String dest =  namedDestination.getNamedDestination();
			pageDestination = catalog.findNamedDestinationPage((PDNamedDestination)namedDestination);
			if( pageDestination != null ){
			    int left = 0;
			    int top = 0;
			    float zoom = 0.0f;
			    if( pageDestination instanceof PDPageXYZDestination ){
				PDPageXYZDestination pageXYZDestination = (PDPageXYZDestination)pageDestination;
				left = pageXYZDestination.getLeft();
				top = pageXYZDestination.getTop();
				zoom = pageXYZDestination.getZoom();
			    }
			    // page = pageDestination.getPage();
			    int npage = pageDestination.retrievePageNumber();
			    System.out.printf(fmt, i, "namedDest", llx, lly, urx, ury, width, height, rotation, npage, left, top, zoom, dest);
			} else {
			    System.out.printf(fmt, i, "UNKNOWN", llx, lly, urx, ury, width, height, rotation, 0, 0, 0.0, "N/A", "N/A");
			}
		    }
		} else {
		    System.out.printf(fmt, i, "UNKNOWN", llx, lly, urx, ury, width, height, rotation, 0, 0, 0.0, "N/A", "N/A");
		}
	    }
	}
    }

    public static void main (String args[]) throws IOException {
       PDDocument doc = null;
       if( args.length == 0 ){
	   System.out.println("usage: java PDFLinks <file>");
	   System.exit(1);
       }
       doc = PDDocument.load(new File(args[0]));
       // output header
       System.out.printf(fmt0, "onPage", "linkType", "rectLLX", "rectLLY", "rectURX", "rectURY", "rectWid", "rectHt", "rectRot", "toPage", "toLeft", "toTop", "toZoom", "namedDestOrURI");
       System.out.printf(fmt0, "------", "---------------", "-------", "-------", "-------", "-------", "-------", "-------", "-------", "------", "------", "-----", "------", "--------------");
       for(int i = 0; i < doc.getNumberOfPages(); i++){
	   PDPage page = doc.getPage(i);
	   listPDFLinks(doc, page, i+1);
       }
       doc.close();
   }  
}
