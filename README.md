# README
## Notes
The **total number of new URLs found (crawled or not crawled)** are equal to the total number of URLs in `res.txt` **and** `res2.txt`, which is equivalent to the total number of lines in `res.txt` and `res2.txt`. Note that for `res2.txt`, the total number of lines should be subtracted by 1 to exclude the title of res2.txt. The summary of the calculation of the total number of new URLs found is as follow:

**Total number of new URLs (crawled or not crawled)**
= Total number of lines in res.txt + Total number of lines in res2.txt - 1

## Execution
To run the program:
1. Place `seed.txt` into `build/libs` folder.
2. In root directory, run `bash run-jar.sh`.
* This will build the jar files using Gradle and run the program as a jar executable.

To create the jar files only (cawler.jar and merger.jar)
* `bash create_jars.sh`
* You can then find the created jar files in `build/libs` folder.

To view bug replay in branch part3_a and part3_b
* Run the program as explained above using the seed file 'seed_p3.txt'
* On the first few URLS that are written into the IUT(in data directory), the source.txt and content.html will contain incorrect/duplicate data as a result of multiple threads writing into it the exact same data.
* You will also see that res.txt will have multiple lines that are exactly the same as a result of data race.
