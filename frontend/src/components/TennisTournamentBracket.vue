<template>
  <div class="tournament-bracket">
    <div v-for="(round, roundIndex) in processedRounds" :key="roundIndex" class="round">
      <h3>{{ getRoundName(roundIndex) }}</h3>
      <div class="matches">
        <div v-for="(match, matchIndex) in round" :key="matchIndex" 
             class="match-wrapper"
             :style="getMatchStyle(roundIndex, matchIndex)">
          <div class="match">
            <div class="player" :class="{ winner: match.player1.winner }">
              <span class="name">{{ match.player1.name }}</span>
              <div class="scores">
                <span v-for="(score, scoreIndex) in match.player1.scores" :key="scoreIndex"
                      :class="{ 'winning-score': score > match.player2.scores[scoreIndex] }">
                  {{ score }}
                </span>
              </div>
            </div>
            <div class="player" :class="{ winner: match.player2.winner }">
              <span class="name">{{ match.player2.name }}</span>
              <div class="scores">
                <span v-for="(score, scoreIndex) in match.player2.scores" :key="scoreIndex"
                      :class="{ 'winning-score': score > match.player1.scores[scoreIndex] }">
                  {{ score }}
                </span>
              </div>
            </div>
          </div>
          <!-- Connectors -->
          <div v-if="roundIndex > 0" class="connector">
            <div class="horizontal-line"></div>
            <div class="vertical-line"></div>
            <div class="horizontal-line horizontal-line-right"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
export default {
  name: 'TennisTournamentBracket',
  data() {
    return {
      rounds: [
        [
          { player1: { name: 'Aaron', scores: [7, 5, 6] }, player2: { name: 'Andre', scores: [5, 7, 4] } },
          { player1: { name: 'Arthur', scores: [7, 6] }, player2: { name: 'Coben', scores: [5, 4] } },
          { player1: { name: 'Siew Yu', scores: [6, 7, 7] }, player2: { name: 'Pauline', scores: [7, 5, 5] } },
          { player1: { name: 'Player 7', scores: [7, 6] }, player2: { name: 'Player 8', scores: [5, 4] } },
          { player1: { name: 'Player 9', scores: [7, 5, 6] }, player2: { name: 'Player 10', scores: [5, 7, 4] } },
          { player1: { name: 'Player 12', scores: [7, 5, 5] }, player2: { name: 'Player 11', scores: [6, 7, 7] } },
          { player1: { name: 'Player 13', scores: [7, 6] }, player2: { name: 'Player 14', scores: [5, 4] } },
          { player1: { name: 'Player 15', scores: [7, 5, 7] }, player2: { name: 'Player 16', scores: [5, 7, 5] } },
        ],
        [
          { player1: { name: 'Aaron', scores: [7, 6] }, player2: { name: 'Arthur', scores: [5, 4] } },
          { player1: { name: 'Siew Yu', scores: [7, 5, 6] }, player2: { name: 'Player 7', scores: [5, 7, 4] } },
          { player1: { name: 'Player 9', scores: [6, 7, 7] }, player2: { name: 'Player 11', scores: [7, 5, 5] } },
          { player1: { name: 'Player 13', scores: [7, 6] }, player2: { name: 'Player 15', scores: [5, 4] } },
        ],
        [
          { player1: { name: 'Aaron', scores: [7, 6] }, player2: { name: 'Siew Yu', scores: [5, 4] } },
          { player1: { name: 'Player 9', scores: [6, 7, 7] }, player2: { name: 'Player 13', scores: [7, 5, 5] } },
        ],
        [
          { player1: { name: 'Aaron', scores: [6, 7, 7] }, player2: { name: 'Player 9', scores: [7, 5, 5] } },
        ]
      ]
    };
  },
  methods: {
    getWinner(player1, player2) {
      const player1Wins = player1.scores.reduce((wins, score, index) => wins + (score > player2.scores[index] ? 1 : 0), 0);
      const player2Wins = player2.scores.reduce((wins, score, index) => wins + (score > player1.scores[index] ? 1 : 0), 0);
      return player1Wins > player2Wins ? player1 : player2;
    },
    getRoundName(index) {
      const names = ['Round of 16', 'Quarter-finals', 'Semi-finals', 'Final'];
      return names[index] || `Round ${index + 1}`;
    },
    getMatchStyle(roundIndex, matchIndex) {
      if (roundIndex === 0) return {};

      const prevRoundMatchCount = this.processedRounds[roundIndex - 1].length;
      const totalHeight = 100; // 100%
      const matchHeight = totalHeight / prevRoundMatchCount;
      const topPosition = matchHeight * (matchIndex * 2 + 1);

      return {
        position: 'absolute',
        top: `${topPosition}%`,
        transform: 'translateY(-50%)'
      };
    }
  },
  computed: {
    processedRounds() {
      return this.rounds.map(round => {
        return round.map(match => {
          const winner = this.getWinner(match.player1, match.player2);
          return {
            ...match,
            player1: { ...match.player1, winner: winner === match.player1 },
            player2: { ...match.player2, winner: winner === match.player2 }
          };
        });
      });
    }
  }
};

</script>

<style scoped>
.tournament-bracket {
  display: flex;
  justify-content: space-between;
  padding: 20px;
  background-color: #f0f0f0;
  font-family: Arial, sans-serif;
}

.round {
  display: flex;
  flex-direction: column;
  width: 23%;
  position: relative;
  margin-right: 2.5em;
}

h3 {
  text-align: center;
  margin-bottom: 10px;
}

.matches {
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.match-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.match {
  background-color: #d3d3d3;
  border-radius: 5px;
  overflow: hidden;
  width: 100%;
  margin: 2em;
}

.player {
  display: flex;
  justify-content: space-between;
  padding: 5px 10px;
  border-bottom: 1px solid #b0b0b0;
}

.player:last-child {
  border-bottom: none;
}

.winner {
  background-color: #c0c0c0;
}

.name {
  font-weight: bold;
}

.scores {
  display: flex;
  gap: 5px;
}

.winning-score {
  color: #008000;
  font-weight: bold;
}

@media (max-width: 768px) {
  .tournament-bracket {
    flex-direction: column;
  }

  .round {
    width: 100%;
    margin-bottom: 20px;
  }

  .match-wrapper {
    position: static !important;
    transform: none !important;
    margin-bottom: 10px;
  }
}
</style>