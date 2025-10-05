import tkinter as tk
from tkinter import messagebox
import json
import os

#okay tkinter is a built in python module that lets me make the front part of my score tracker
#the as tk part i used so that i could use a tk.button making it a little easier
#i imported a messagebox so that it would show a popup window letting me know if a game was recorded or not
#then i had to import a json file that holds all the different players data

#alright, this is my first class that will create objects and store different players stats and manage their data
class PickleballTracker:
    #these are all of constructors
    def __init__(self, filename="pickleball_stats.json"):
        self.filename = filename
        self.data = {}
        self.player = None
        self.load_data()

    #this will set the player
    def set_player(self, player_name):
        """Set the current player and create their record if new."""
        self.player = player_name.strip().title()
        if self.player not in self.data:
            self.data[self.player] = []

    #this will load the data 
    def load_data(self):
        if os.path.exists(self.filename):
            with open(self.filename, "r") as file:
                self.data = json.load(file)

            #this checks if old data is a list
            if isinstance(self.data, list):
                print("⚠️ Detected old format — converting to new player-based format.")
                self.data = {"DefaultPlayer": self.data}
                self.save_data()
        else:
            self.data = {}

    #this saves the data
    def save_data(self):
        with open(self.filename, "w") as file:
            json.dump(self.data, file, indent=4)

    #this is for recording a game 
    #it checks if the player has been set
    def record_game(self, my_score, opponent_score):
        if not self.player:
            #this sets the result to win if the player scored higher or loss if you know, they played bad and didn't win...
            raise ValueError("Player name not set.")
        result = "Win" if my_score > opponent_score else "Loss"
        #this makes a dictionary to represent one game
        game = {
            "my_score": my_score,
            "opponent_score": opponent_score,
            "result": result
        }
        #this is what adds the game to a players list
        #then it saves it
        #and then returns the result string
        self.data[self.player].append(game)
        self.save_data()
        return result

    #this shows all of those stats
    def stats(self, player_name=None):
        """Return stats for the current or a specific player."""
        player = player_name or self.player
        if not player or player not in self.data:
            return {"Error": "No data for this player."}

        #games is a list of all recorded games
        #it then counts how many wins or losses someone gets
        games = self.data[player]
        wins = sum(1 for g in games if g["result"] == "Win")
        losses = sum(1 for g in games if g["result"] == "Loss")
        #calculates the total number of games
        total = wins + losses
        win_ratio = wins / losses if losses > 0 else wins
        win_percentage = (wins / total) * 100 if total > 0 else 0

        #this then returns a dictionary summarizing all of that said players stats
        return {
            "Player": player,
            "Wins": wins,
            "Losses": losses,
            "Games Played": total,
            "Win/Loss Ratio": round(win_ratio, 2) if total > 0 else 0,
            "Win %": round(win_percentage, 2)
        }

    #i wanted to add this because i thought it would be fun
    #this loops through all of the players in thedata and gets their stats
    #it also skips if there are errors
    def get_rankings(self):
        """Return all players ranked by win percentage."""
        rankings = []
        for player in self.data:
            stats = self.stats(player)
            if "Error" not in stats:
                rankings.append(stats)
        # Sort by Win %, then by Wins
        rankings.sort(key=lambda s: (s["Win %"], s["Wins"]), reverse=True)
        return rankings

#this is the tkinter window class that connects the buttons and inputs to the tracker
#this was a total new learning curve for me
#i have never done anything like this before so it took a lot of trial and error
class PickleballApp:
    #all my constructors
    def __init__(self, root, player_name):
        self.tracker = PickleballTracker()
        self.tracker.set_player(player_name)
        self.root = root
        self.root.title(f"Pickleball Tracker - {player_name}")

        #this is the label showing the players name
        tk.Label(root, text=f"Player: {player_name}", font=("Arial", 12, "bold")).grid(row=0, column=0, columnspan=2, pady=10)

        #this creates a label and a small text box where you can type your score
        tk.Label(root, text="Your Score:").grid(row=1, column=0, padx=10, pady=5)
        self.my_score_entry = tk.Entry(root, width=10)
        self.my_score_entry.grid(row=1, column=1, padx=10, pady=5)

        tk.Label(root, text="Opponent Score:").grid(row=2, column=0, padx=10, pady=5)
        self.opponent_score_entry = tk.Entry(root, width=10)
        self.opponent_score_entry.grid(row=2, column=1, padx=10, pady=5)

        #these are all the buttons
        #boo yeah
        tk.Button(root, text="Record Game", command=self.record_game).grid(row=3, column=0, columnspan=2, pady=10)
        tk.Button(root, text="View Stats", command=self.show_stats).grid(row=4, column=0, columnspan=2, pady=5)
        tk.Button(root, text="View Rankings", command=self.show_rankings).grid(row=5, column=0, columnspan=2, pady=10)

        #this starts blank but will show tect later just like stats
        self.stats_label = tk.Label(root, text="", justify="left", font=("Arial", 12))
        self.stats_label.grid(row=6, column=0, columnspan=2, padx=10, pady=10)

    #this records game
    #it reads both score boxes and converts them to numbers
    #then it shows a popup message box with the results
    def record_game(self):
        try:
            my_score = int(self.my_score_entry.get())
            opponent_score = int(self.opponent_score_entry.get())
            result = self.tracker.record_game(my_score, opponent_score)
            messagebox.showinfo("Game Recorded", f"Game recorded as a {result} ({my_score}-{opponent_score})")
            self.my_score_entry.delete(0, tk.END)
            self.opponent_score_entry.delete(0, tk.END)
        except ValueError:
            messagebox.showerror("Input Error", "Please enter valid integer scores.")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    #this shows all them stats
    def show_stats(self):
        stats = self.tracker.stats()
        stats_text = "\n".join([f"{k}: {v}" for k, v in stats.items()])
        self.stats_label.config(text=stats_text)

    #this shows each individual players ranking with other players
    def show_rankings(self):
        rankings = self.tracker.get_rankings()
        if not rankings:
            messagebox.showinfo("Rankings", "No data available yet.")
            return

        leaderboard = "Player Rankings \n\n"
        for i, s in enumerate(rankings, start=1):
            leaderboard += f"{i}. {s['Player']} - {s['Win %']}% Win ({s['Wins']}W/{s['Losses']}L)\n"

        messagebox.showinfo("Rankings", leaderboard)

#this was the last part, it starts the program 
if __name__ == "__main__":
    player_name = input("Enter your player name: ").strip()
    if not player_name:
        print("Player name is required to start.")
    else:
        root = tk.Tk()
        app = PickleballApp(root, player_name)
        root.mainloop()
