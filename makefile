GITBASH=C:/Program Files/Git/usr/bin/sh.exe
JAVA_ARGS=""
ifeq ($(SHELL),$(GITBASH))
default: ## 'make' will clean, compile, and run the project.
default: clean classes run

editor: ## 'make editor' will run the planet editor.
editor: JAVA_ARGS="--editor"
editor: default

graphicsTest: ## 'make graphicsTest' will run the planet editor.
graphicsTest: JAVA_ARGS="--graphicsTest"
graphicsTest: default

classes: ## 'make classes' will compile the project.
				javac -classpath ".:.\lib\gluegen-rt.jar:.\lib\jogl-all.jar" bin/ClientMain.java

clean: ## 'make clean' will remove all compiled java sources.
				find . -name *.class -type f -delete

run: ## 'make run' will run the compiled project.
				java -classpath ".:.\lib\gluegen-rt.jar:.\lib\jogl-all.jar" -Djava.library.path="/natives" bin/ClientMain.java $(JAVA_ARGS)

report: ## 'make report' prints the os
				@echo "Git Bash"

help: ## 'make help' shows this help message
				@echo "here are the valid make targets:"
				@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'

else
default: clean classes run

editor: JAVA_ARGS="--editor"
editor: default

graphicsTest: JAVA_ARGS="--graphicsTest"
graphicsTest: default

classes:
				javac -classpath ".;lib\gluegen-rt.jar;lib\jogl-all.jar" bin/ClientMain.java

clean:
				powershell "ls *.class -Recurse | foreach {rm $$PSItem}"

run:
				java -classpath ".;lib\gluegen-rt.jar;lib\jogl-all.jar" -D"java.library.path"="/natives" bin/ClientMain.java $(JAVA_ARGS)

report: ## 'make report' prints the os
				@echo "Powershell"

help:
				@echo here are the valid make targets:
				@powershell 'cat makefile | Select-String -Pattern "##" | Select-String -NotMatch -Pattern "fgrep|Select-String"'
endif
