<template>
    <div class="vtb-wrapper" v-if="recursiveBracket">
        <bracket-node
            :bracket-node="recursiveBracket"
            @onSelectedPlayer="highlightPlayer"
            @onDeselectedPlayer="unhighlightPlayer"
            :highlighted-player-id="highlightedPlayerId"
        >
            <!-- Slot for displaying player name and score boxes -->
            <template #player="{ player }">
                <div class="player-box">
                    <span class="player-name">{{ player.name }}</span>
                    <div class="score-box">
                        <span
                            v-for="(score, index) in player.scores"
                            :key="index"

                            class="score"
                        >
                            {{ score }}
                        </span>
                    </div>
                </div>
            </template>

            <!-- Slot for player extension bottom if needed -->
            <template #player-extension-bottom="{ match }">
                <slot name="player-extension-bottom" :match="match" />
            </template>
        </bracket-node>
    </div>
</template>

<script>
import BracketNode from "@/components/BracketNode";
import recursiveBracket from "@/components/recursiveBracket";

export default {
    name: "bracket-page",
    components: {
        "bracket-node": BracketNode,
    },
    props: ["rounds", "flatTree"],
    data() {
        return {
            highlightedPlayerId: null,
        };
    },
    computed: {
        recursiveBracket() {
            if (this.flatTree) {
                return recursiveBracket.transformFlatTree(this.flatTree);
            }

            return recursiveBracket.transform(this.rounds);
        },
    },
    methods: {
        highlightPlayer(id) {
            this.highlightedPlayerId = id;
        },
        unhighlightPlayer() {
            this.highlightedPlayerId = null;
        },
        getScoreClass(player, index) {
            // Get the opponent player
            const opponent = player === this.recursiveBracket.player1 ? this.recursiveBracket.player2 : this.recursiveBracket.player1;
            const playerScore = player.scores[index];
            const opponentScore = opponent.scores[index];

            // Highlight the higher score
            if (playerScore > opponentScore) {
                return "highlight-score";
            }
            return "score";
        }
    },
};
</script>

<style>
.vtb-wrapper {
    display: flex;
}

.player-box {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.player-name {
    margin-right: 10px;
    font-weight: 500;
}

.score-box {
    display: flex;
    gap: 3px;
}

.score {
    padding: 2px 8px;
    background-color: #ff6f43a9;
    color: white;
    border-radius: 4px;
}

/* Highlighted score box */
.highlight-score {
    background-color: #ffd700; /* Gold color for higher score */
}
</style>
