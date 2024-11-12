<template>
    <div class="organizer-dashboard">
        <Navbar />
        <h2>My Tournaments</h2>
        <TournamentCard v-for="tournament in tournaments" :key="tournament.id" :tournament="tournament" role="organizer" :userId="userId" />
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
            const response = await axios.get(`http://localhost:8080/tournaments/organizerId/${this.userId}`);
            this.tournaments = response.data;
        },
    },
};
</script>
