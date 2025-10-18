#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include "Hangman.h"

// Reads words from words.txt into a vector (file I/O stretch)
std::vector<std::string> loadWordsFromFile(const std::string& filename) {
    std::vector<std::string> result;
    std::ifstream in(filename);
    if (!in) {
        std::cerr << "Warning: could not open " << filename << ". Using default words.\n";
        // fallback list
        result = {"default", "example", "hangman"};
        return result;
    }
    std::string w;
    while (std::getline(in, w)) {
        // trim whitespace and ignore empty lines
        if (!w.empty()) {
            // to lower
            for (char &c : w) c = static_cast<char>(std::tolower(static_cast<unsigned char>(c)));
            result.push_back(w);
        }
    }
    if (result.empty()) {
        result = {"default", "example", "hangman"};
    }
    return result;
}

int main() {
    std::cout << "Loading words from words.txt...\n";
    auto words = loadWordsFromFile("words.txt");
    Hangman game(words);

    // play multiple rounds with simple loop and prompt (demonstrates loops + conditionals)
    while (true) {
        game.play();
        std::cout << "Play again? (y/n): ";
        char ans;
        std::cin >> ans;
        ans = static_cast<char>(std::tolower(static_cast<unsigned char>(ans)));
        if (ans != 'y') break;
    }

    std::cout << "Thanks for playing!\n";
    return 0;
}
