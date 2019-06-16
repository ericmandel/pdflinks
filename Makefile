JARDIR =	$$HOME/Library/Java/Extensions

PROGS =		PDFLinks.class

all:		$(PROGS)

PDFLinks.class:	PDFLinks.java
		javac -Xlint PDFLinks.java

installjar:	FORCE
		@(mkdir -p $(JARDIR) && cp -p jarfiles/*.jar $(JARDIR)/.)

view-readme:	FORCE
		@(grip)

clean:		FORCE
		@($(RM) foo* *~ *.bak *.log */*~)

FORCE:
