PDFLinks: read links in a PDF file, add links to a stripped file
================================================================

Not really meant for public consumption ... but you're welcome to it ...

Which Java?
-----------

This code was developed with Java 8/11 on MacOS. I have successfully used two
OpenJDK installations of Java 11:

    # Adopt Open JDK (currently in use):
    https://adoptopenjdk.net/
    https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_x64_mac_hotspot_11.0.3_7.pkg

    # Azul Systems:
    https://www.azul.com/downloads/zulu/
    https://cdn.azul.com/zulu/bin/zulu11.31.11-ca-jdk11.0.3-macosx_x64.dmg

To install on a Mac:
--------------------

    # get repository
    git clone https://github.com/ericmandel/pdflinks
    cd pdflinks

    # generate the PDFLinks.class file
    make

To run, use the pdflinks script, which hides the nasty java details:
--------------------------------------------------------------------

    # generate list of existing links and anchors in tab-delimited text format
    pdflinks i_file.pdf > links.txt

    # generate list of existing links or anchors in tab-delimited text format
    pdflinks i_file.pdf [links|anchors]> links.txt

    # optional: incorporate debugging style (colored border) when adding links
    export PDFLINKS_DEBUG=1

    # add links to a previously-stripped file
    pdflinks i_stripped.pdf o_withlinks.pdf links.txt [borderWidth (def:0)]

What's the license?
-------------------

Distributed under the terms of The MIT License.

Who's responsible?
------------------

Eric Mandel, Alexey Vikhlinin

Center for Astrophysics | Harvard & Smithsonian
