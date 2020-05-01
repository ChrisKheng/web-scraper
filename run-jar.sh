# Delete old output files first
rm res.txt
rm res2.txt
rm statistics.txt
rm -r data

# Build the jar file first
./gradlew shadowJar2
./gradlew shadowJar3

cd build/libs

# Run the jar file
java -jar crawler.jar -time 1h -input seed.txt -output res.txt -storedPageNum 1000