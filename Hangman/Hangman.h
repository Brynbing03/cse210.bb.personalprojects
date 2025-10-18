// HangmanGame.h
#ifndef HANGMAN_GAME_H
#define HANGMAN_GAME_H

#include <string>
#include <set>

class HangmanGame {
public:
    HangmanGame(const std::string &secretWord, int maxAttempts = 6);
    void play();                    // main game loop
    bool isWon() const;
    bool isLost() const;
    std::string getMaskedWord() const;
    int getRemainingAttempts() const;
    std::string getSecretWord() const;

private:
    std::string secret;
    std::string masked;   // e.g. "_ p p _ e"
    std::set<char> guessed;
    int attemptsLeft;
    void updateMasked(char guess);
    bool processGuess(char guess);
    void displayState() const;
};

#endif // HANGMAN_GAME_H
