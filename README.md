PDFLinks: read links in a PDF file, add links to a stripped file
================================================================

Not really meant for public consumption ... but you're welcome to it ...

Which Java?
-----------

This code works with Java 8. I used the OpenJDK version from Azul Systems:

    https://www.azul.com/downloads/zulu/

Here is the download link I used:

     https://cdn.azul.com/zulu/bin/zulu8.38.0.13-ca-jdk8.0.212-macosx_x64.dmg

To install on a Mac:
--------------------

    # get repository
    git clone https://github.com/ericmandel/pdflinks
    cd pdflinks

    # one time install: jar files
    make installjar

    # make the class file
    make

To run:
-------

    # generate list of existing links and anchors in tab-delimited text format
    java PDFLinks i_file.pdf > links.txt

    # generate list of existing links or anchors in tab-delimited text format
    java PDFLinks i_file.pdf [links|anchors]> links.txt

    # optional: incorporate debugging style (colored border) when adding links
    export PDFLINKS_DEBUG=1

    # add links to a previously-stripped file
    java PDFLinks i_stripped.pdf o_withlinks.pdf links.txt

What's the license?
-------------------

Distributed under the terms of The MIT License.

Who's responsible?
------------------

Eric Mandel, Alexey Vikhlinin

Center for Astrophysics | Harvard & Smithsonian
