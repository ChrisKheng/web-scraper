# Build the jar file first
./gradlew shadowJar

# Run the jar file
java -jar ./build/libs/web-scraper-all.jar < seeds.txt