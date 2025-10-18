// main.cpp
#include <iostream>
#include <fstream>
#include <vector>
#include <random>
#include <ctime>
#include "HangmanGame.h"

std::vector<std::string> loadWordsFromFile(const std::string &filename) {
    std::vector<std::string> words;
    std::ifstream in(filename);
    if (!in) {
        std::cerr << "Could not open " << filename << ". Using default words.\n";
        words = {"apple", "banana", "computer", "hangman", "program"};
        return words;
    }
    std::string w;
    while (std::getline(in, w)) {
        if (!w.empty()) words.push_back(w);
    }
    return words;
}

std::string pickRandomWord(const std::vector<std::string> &words) {
    if (words.empty()) return "default";
    static std::mt19937 rng((unsigned)std::time(nullptr));
    std::uniform_int_distribution<size_t> dist(0, words.size()-1);
    return words[dist(rng)];
}

void saveResult(const std::string &filename, const std::string &word, bool won) {
    std::ofstream out(filename, std::ios::app);
    if (!out) return;
    out << word << "," << (won ? "W" : "L") << "\n";
}

int main() {
    std::cout << "Welcome to Hangman!\n";
    auto words = loadWordsFromFile("words.txt");

    bool playAgain = true;
    while (playAgain) {
        std::string secret = pickRandomWord(words);
        HangmanGame game(secret, 6);
        game.play();
        bool won = game.isWon();
        saveResult("scores.txt", secret, won);

        std::cout << "Play again? (y/n): ";
        char c; std::cin >> c;
        playAgain = (c == 'y' || c == 'Y');
    }

    std::cout << "Thanks for playing!\n";
    return 0;
}
