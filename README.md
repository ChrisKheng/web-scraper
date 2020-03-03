# ReadMe
To set up and run the project, run the command `bash run.sh` in the root directory.

Must provide a seeds file or else the program may hang forever.

The seeds file needs to be:
* in the root directory
* named as `seeds.txt`, with each url on separate lines.


## Ways to run the program
1. `bash run.sh`
* This will use gradle wrapper to run the program.

2. `bash run-jar.sh`
* This will build the jar file and run the program as a jar executable.
* To buld the jar only, run `./gradlew shadowJar`. You can then access the jar file in `build/lib` folder.
