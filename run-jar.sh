# Delete old output files first
rm res.txt
rm res2.txt
rm statistics.txt
rm -r data

# Build the jar file first
./gradlew shadowJar

cd build/libs

# Run the jar file
java -jar Cawler.jar -time 1h -input seed.txt -output res.txt
