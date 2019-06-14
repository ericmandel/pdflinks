PDFLinks: read links in a PDF file, add links to a stripped file
================================================================

Not really meant for public consumption ... but you're welcome to it ...

To install on a Mac:
--------------------

    # get repository
    git clone https://github.com/ericmandel/pdflinks
    cd pdflinks

    # one time only: install support jar files (Mac: ~/Library/Java/Extensions)
    make installjar

    #  make the class file: not necessary, since class file is supplied
    make all

To run:
-------

    # generate list of existing links in tab-delimited text format
    java PDFLinks i_file.pdf > links.txt

    # optional: incorporate debugging style (colored border) when adding links
    export PDFLINKS_DEBUG=1

    # add links to a stripped file
    java PDFLinks i_stripped.pdf o_stripped_withlinks.pdf links.txt

What's the license?
-------------------

Distributed under the terms of The MIT License.

Who's responsible?
------------------

Eric Mandel, Alexey Vikhlinin

Center for Astrophysics | Harvard & Smithsonian
