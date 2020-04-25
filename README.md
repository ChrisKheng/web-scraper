# ReadMe
To run the program:
1. Place `seed.txt` into `build/libs` folder.
2. In root directory, run `bash run-jar.sh`.
* This will build the jar file and run the program as a jar executable.
* To buld the jar only, run `./gradlew shadowJar`. You can then access the jar file in `build/libs` folder.

To create the jars only (cawler.jar and merger.jar)
* `bash create_jars.sh`
* You can then find the created jars in `build/libs` folder.

To view bug replay in branch part3_a and part3_b
* Run the program as explained above using the seed file 'seed_p3.txt'
* On the first few URLS that are written into the IUT(in data directory), the source.txt and content.html will contain incorrect/duplicate data as a result of multiple threads writing into it the exact same data.
* You will also see that res.txt will have multiple lines that are exactly the same as a result of data race.