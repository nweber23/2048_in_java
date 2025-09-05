# 2048 Java Implementation

A console-based implementation of the popular 2048 game in Java.

## Features

- Standard 2048 gameplay on 4×4 or 5×5 grid
- Colorful display using ANSI escape codes
- ASCII art number rendering option
- Menu system with high scores
- Persistent score saving
- Resize handling

## Build Instructions

### Using Gradle

1. Navigate to the project directory
2. Build the project: `./gradlew build`
3. Run the game: `java -jar build/libs/2048.jar`

### Using Maven

1. Navigate to the project directory
2. Build the project: `mvn package`
3. Run the game: `java -jar target/2048.jar`

## Command Line Options

- `-s <size>`: Set the grid size (4 or 5), e.g., `java -jar 2048.jar -s 5`

## Game Controls

- Arrow keys or WASD: Move tiles
- ESC or Q: Quit the game
- Ctrl+L: Redraw screen (useful after resize)

## Spawn Probability

New tiles spawn with:
- 90% probability of being a 2
- 10% probability of being a 4

## Menu Navigation

- Use arrow keys or W/S to navigate the menu
- Press Enter to select an option
- Options include:
  - Start Game: Begin a new game
  - Options: Toggle between color and ASCII art modes
  - High Scores: View the top 5 scores
  - Quit: Exit the game

## Score Saving

High scores are automatically saved to `scores.txt` in the same directory as the JAR file. The top 5 scores are preserved.

## Requirements

- Java 17 or higher
- Terminal that supports ANSI escape codes (most modern terminals do)
