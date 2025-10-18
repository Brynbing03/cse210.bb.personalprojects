// HangmanGame.cpp
#include "HangmanGame.h"
#include <iostream>
#include <algorithm>

HangmanGame::HangmanGame(const std::string &secretWord, int maxAttempts)
 : secret(secretWord), attemptsLeft(maxAttempts)
{
    masked = std::string(secret.size(), '_');
}

void HangmanGame::play() {
    while (!isWon() && !isLost()) {
        displayState();
        std::cout << "Enter a letter: ";
        char c;
        std::cin >> c;
        c = std::tolower(c);
        if (!std::isalpha(c)) {
            std::cout << "Please enter a letter (a-z).\n";
            continue; // loop again
        }
        if (guessed.count(c)) {
            std::cout << "You already guessed '" << c << "'. Try another.\n";
            continue;
        }
        bool correct = processGuess(c);
        if (correct) std::cout << "Nice!\n";
        else {
            std::cout << "Wrong!\n";
            attemptsLeft--;
        }
    }

    if (isWon()) {
        std::cout << "Congratulations! You guessed: " << secret << "\n";
    } else {
        std::cout << "Out of attempts. The word was: " << secret << "\n";
    }
}

bool HangmanGame::processGuess(char guess) {
    guessed.insert(guess);
    bool found = false;
    for (size_t i = 0; i < secret.size(); ++i) {
        if (std::tolower(secret[i]) == guess) {
            masked[i] = secret[i];
            found = true;
        }
    }
    return found;
}

void HangmanGame::displayState() const {
    std::cout << "\nWord: ";
    for (char ch : masked) std::cout << ch << ' ';
    std::cout << "\nGuessed: ";
    for (char g : guessed) std::cout << g << ' ';
    std::cout << "\nAttempts left: " << attemptsLeft << "\n\n";
}

bool HangmanGame::isWon() const {
    return masked == secret;
}

bool HangmanGame::isLost() const {
    return attemptsLeft <= 0;
}

std::string HangmanGame::getMaskedWord() const { return masked; }
int HangmanGame::getRemainingAttempts() const { return attemptsLeft; }
std::string HangmanGame::getSecretWord() const { return secret; }
