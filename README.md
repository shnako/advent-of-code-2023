# Advent of Code 2023 :christmas_tree:

I tried to make the code as clean and easy to read as possible, and I've also provided a short explanation to most solutions so that they're easier to understand.

| Day | Puzzle                                             | Solution                                                                   | Results                                                                    | Input                                                               | Text                                                                  |
|-----|----------------------------------------------------|----------------------------------------------------------------------------|----------------------------------------------------------------------------|---------------------------------------------------------------------|-----------------------------------------------------------------------|
| 01  | [Trebuchet?!](https://adventofcode.com/2023/day/1) | [Day 01 solution](/src/main/java/com/shnako/solutions/day01/Solution.java) | [Day 01 test](/src/test/java/com/shnako/solutions/day01/SolutionTest.java) | [Day 01 input](/src/main/java/com/shnako/solutions/day01/input.txt) | [Day 01 puzzle](/src/main/java/com/shnako/solutions/day01/puzzle.txt) |


## Structure

### [Solutions](/src/main/java/com/shnako/solutions)
The corresponding files for each solution are grouped within a dayXX package. Each package contains:
- Solution.java: my implementation of the solution to the puzzle
- puzzle.txt: a dump of the puzzle text from the website, with the source link at the top
- input.txt: the puzzle input I received

Each solution extends the [SolutionBase](/src/main/java/com/shnako/solutions/SolutionBase.java) interface and must implement the [runPart1](/src/main/java/com/shnako/solutions/SolutionBase.java#L6) and [runPart2](/src/main/java/com/shnako/solutions/SolutionBase.java#L8) methods which return the result as a string.

### [Tests and results](/src/test/java/com/shnako/solutions)
Each test implements the [SolutionBaseTest](/src/test/java/com/shnako/SolutionBaseTest.java) interface and must specify the expected results in the test assertions.

## Running

### All solutions via tests
You can run all the tests using gradle:

    ./gradlew test

or if you're using Windows:

    gradlew.bat test

### Individual solutions via main
You can also take advantage of the functionality implemented in [Main.java](/src/main/java/com/shnako/Main.java) to run the puzzles individually:

    ./gradlew run --args='<day> <part>'

or if you're using Windows:

    gradlew.bat run --args="<day> <part>"

replacing `<day>` with the puzzle's day number and `<part>` with either 1 or 2, or 0 to run both parts.

Running without the arguments will prompt you to enter them.