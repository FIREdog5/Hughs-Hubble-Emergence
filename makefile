GITBASH := C:/Program Files/Git/usr/bin/sh.exe
JAVA_CLASSPATH := .
LIB_FILES := $(shell find lib -type f -name '*.jar' -exec echo -n :./{} \;)
JAVA_CLASSPATH := "$(JAVA_CLASSPATH)$(LIB_FILES)"
JAVA_CLASSPATH := $(subst /,\,$(JAVA_CLASSPATH))
JAVA_ARGS := ""
ifeq ($(SHELL),$(GITBASH))
default: ## 'make' will clean, compile, and run the project.
default: clean classes run

.PHONY: editor
editor: default ## 'make editor' will run the planet editor.
.PHONY: graphicsTest
graphicsTest: default ## 'make graphicsTest' will run the graphics sandbox.
.PHONY: borderless
borderless: ## 'nake borderless' will launch the game in borderless mode. This is not a standalone target.
borderless:
.PHONY: bordered
bordered: ## 'nake bordered' will launch the game in border mode. This is not a standalone target.
bordered:

classes: ## 'make classes' will compile the project.
				javac -classpath $(JAVA_CLASSPATH) bin/ClientMain.java

clean: ## 'make clean' will remove all compiled java sources.
				find . -name *.class -type f -delete

run: ## 'make run' will run the compiled project.
				java -classpath $(JAVA_CLASSPATH) -Djava.library.path="/natives" bin/ClientMain.java $(MAKECMDGOALS)

report: ## 'make report' prints the os
				@echo "Git Bash"

help: ## 'make help' shows this help message
				@echo "here are the valid make targets:"
				@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'

else
JAVA_CLASSPATH := $(subst :,;,$(JAVA_CLASSPATH))
default: clean classes run

.PHONY: editor
editor: default
.PHONY: graphicsTest
graphicsTest: default
.PHONY: borderless
borderless:
.PHONY: bordered
bordered:

classes:
				javac -classpath $(JAVA_CLASSPATH) bin/ClientMain.java

clean:
				powershell "ls *.class -Recurse | foreach {rm $$PSItem}"

run:
				java -classpath $(JAVA_CLASSPATH) -D"java.library.path"="/natives" bin/ClientMain.java $(MAKECMDGOALS)

report: ## 'make report' prints the os
				@echo "Powershell"

help:
				@echo here are the valid make targets:
				@powershell 'cat makefile | Select-String -Pattern "##" | Select-String -NotMatch -Pattern "fgrep|Select-String"'
endif
