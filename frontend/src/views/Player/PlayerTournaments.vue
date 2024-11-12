<template>
    <div class="player-dashboard">
        <Navbar />
        <h2>Available Tournaments</h2>
        <TournamentCard v-for="tournament in tournaments" :key="tournament.id" :tournament="tournament" role="player" :userId="userId" />
    </div>
</template>

<script>
import axios from 'axios';

import TournamentCard from '@/components/TournamentCard.vue';

export default {
    components: { TournamentCard },
    data() { return { tournaments: [], userId: localStorage.getItem('userId') }; },
    mounted() { this.fetchTournaments(); },
    methods: {
        async fetchTournaments() {
            const response = await axios.get('http://localhost:8080/tournaments');
            this.tournaments = response.data;
        },
    },
};
</script>
