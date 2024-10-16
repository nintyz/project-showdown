<template>
    <div class="vtb-item" v-if="playersArePresent">
        <div :class="getBracketNodeClass(bracketNode)">
            <game-players :bracket-node="bracketNode" :highlighted-player-id="highlightedPlayerId"
                @onSelectedPlayer="highlightPlayer" @onDeselectedPlayer="unhighlightPlayer">
                <!-- Player slot with name and scores -->
                <template #player="{ player }">
                    <slot name="player" :player="player">
                        <div class="player-box">
                            <span class="player-name">{{ player.name }}</span>
                            <div class="score-box">
                                <span v-for="(score, index) in player.scores" :key="index" class="score">
                                    {{ score }}
                                </span>
                            </div>
                        </div>
                    </slot>
                </template>

                <!-- Player extension bottom slot -->
                <template #player-extension-bottom="{ match }">
                    <slot name="player-extension-bottom" :match="match" />
                </template>
            </game-players>
        </div>

        <div v-if="bracketNode.games[0] || bracketNode.games[1]" class="vtb-item-children">
            <div class="vtb-item-child" v-if="bracketNode.games[0]">
                <bracket-node :bracket-node="bracketNode.games[0]" :highlighted-player-id="highlightedPlayerId"
                    @onSelectedPlayer="highlightPlayer" @onDeselectedPlayer="unhighlightPlayer">
                    <!-- Player slot for child game with name and scores -->
                    <template #player="{ player }">
                        <slot name="player" :player="player">
                            <div class="player-box">
                                <span class="player-name">{{ player.name }}</span>
                                <div class="score-box">
                                    <span v-for="(score, index) in player.scores" :key="index" class="score">
                                        {{ score }}
                                    </span>
                                </div>
                            </div>
                        </slot>
                    </template>
                    <template #player-extension-bottom="{ match }">
                        <slot name="player-extension-bottom" :match="match" />
                    </template>
                </bracket-node>
            </div>

            <div class="vtb-item-child" v-if="bracketNode.games[1]">
                <bracket-node :bracket-node="bracketNode.games[1]" :highlighted-player-id="highlightedPlayerId"
                    @onSelectedPlayer="highlightPlayer" @onDeselectedPlayer="unhighlightPlayer">
                    <!-- Player slot for second child game with name and scores -->
                    <template #player="{ player }">
                        <slot name="player" :player="player">
                            <div class="player-box">
                                <span class="player-name">{{ player.name }}</span>
                                <div class="score-box">
                                    <span v-for="(score, index) in player.scores" :key="index" class="score">
                                        {{ score }}
                                    </span>
                                </div>
                            </div>
                        </slot>
                    </template>
                    <template #player-extension-bottom="{ match }">
                        <slot name="player-extension-bottom" :match="match" />
                    </template>
                </bracket-node>
            </div>
        </div>
    </div>
</template>

<script>
import GamePlayers from "./GamePlayers";

export default {
    name: "bracket-node",
    components: { GamePlayers },
    props: ["bracketNode", "highlightedPlayerId"],
    computed: {
        playersArePresent() {
            return this.bracketNode.player1 && this.bracketNode.player2;
        },
    },
    methods: {
        getBracketNodeClass(bracketNode) {
            if (bracketNode.games[0] || bracketNode.games[1]) {
                return "vtb-item-parent";
            }

            if (bracketNode.hasParent) {
                return "vtb-item-child";
            }

            return "";
        },
        getPlayerClass(player) {
            if (player.winner === null || player.winner === undefined) {
                return "";
            }

            let clazz = player.winner ? "winner" : "defeated";

            if (this.highlightedPlayerId === player.id) {
                clazz += " highlight";
            }

            return clazz;
        },
        highlightPlayer(playerId) {
            this.$emit("onSelectedPlayer", playerId);
        },
        unhighlightPlayer() {
            this.$emit("onDeselectedPlayer");
        },
    },
};
</script>

<style>
.player-box {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.player-name {
    /* padding: 10px;
    background-color: #333; */
    /* color: white;
    margin-right: 10px;
    border-radius: 4px; */
}

.score-box {
    display: flex;
    /* gap: 5px; */
}



.vtb-item {
    display: flex;
    flex-direction: row-reverse;
}

.vtb-item p {
    padding: 20px;
    margin: 0;
    background-color: #999999;
}

.vtb-item-parent {
    position: relative;
    margin-left: 50px;
    display: flex;
    align-items: center;
}

.vtb-item-players {
    flex-direction: column;
    background-color: #999999;
    width: 210px;
    margin: 0;
    color: white;
}

.vtb-item-players .vtb-player {
    padding: 10px;
}

.vtb-item-players .winner {
    background-color: #495057;
}

.vtb-item-players .defeated {
    background-color: #ced4da;
    color: black !important;
}

.vtb-item-players .winner.highlight {
    background-color: #6c757d;
}

.vtb-item-players .defeated.highlight {
    background-color: #e9ecef;
}

.vtb-item-parent:after {
    position: absolute;
    content: "";
    width: 25px;
    height: 2px;
    left: 0;
    top: 50%;
    background-color: gray;
    transform: translateX(-100%);
}

.vtb-item-children {
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.vtb-item-child {
    display: flex;
    align-items: flex-start;
    justify-content: flex-end;
    margin-top: 10px;
    margin-bottom: 10px;
    position: relative;
}

.vtb-item-child:before {
    content: "";
    position: absolute;
    background-color: gray;
    right: 0;
    top: 50%;
    transform: translateX(100%);
    width: 25px;
    height: 2px;
}

.vtb-item-child:after {
    content: "";
    position: absolute;
    background-color: gray;
    right: -25px;
    height: calc(50% + 22px);
    width: 2px;
    top: 50%;
}

.vtb-item-child:last-child:after {
    transform: translateY(-100%);
}

.vtb-item-child:only-child:after {
    display: none;
}
</style>
