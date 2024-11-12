<template>
    <div class="admin-dashboard">
        <Navbar />
        <h2>All Tournaments</h2>
        <button @click="addNewTournament">Add New Tournament</button>
        <TournamentCard v-for="tournament in tournaments" :key="tournament.id" :tournament="tournament" role="admin" @tournamentDeleted="fetchTournaments" />
    </div>
</template>

<script>
import axios from 'axios';

import TournamentCard from '@/components/TournamentCard.vue';

export default {
    components: { TournamentCard },
    data() { return { tournaments: [] }; },
    mounted() { this.fetchTournaments(); },
    methods: {
        async fetchTournaments() {
            const response = await axios.get('http://localhost:8080/tournaments');
            this.tournaments = response.data;
        },
        addNewTournament() { this.$router.push('/new-tournament'); },
    },
};
</script>
