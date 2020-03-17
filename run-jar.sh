# Delete old output files first
rm result.txt
rm result2.txt
rm statistics.txt
rm -r data

# Build the jar file first
./gradlew shadowJar

# Run the jar file
java -jar ./build/libs/web-scraper-all.jar < seeds.txt