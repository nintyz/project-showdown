<template>
    <Navbar />
    <div class="tournaments-page">
        <!-- Check if the user is an organizer -->
        <div v-if="isOrganizer">
            <b-tabs v-model="activeTab">
                <b-tab title="My Tournaments">
                    <!-- Display tournaments organized by the user -->
                    <div v-for="tournament in myTournaments" :key="tournament.id" class="tournament-item">
                        <h3>{{ tournament.name }}</h3>
                        <p>{{ tournament.venue }} | {{ tournament.dateTime }}</p>
                        <div class="action-buttons">
                            <router-link :to="`/tournament/${tournament.id}`" class="btn btn-primary">View</router-link>
                            <button class="btn btn-secondary" @click="editTournament(tournament.id)">Edit</button>
                            <button class="btn btn-danger" @click="cancelTournament(tournament.id)">Cancel</button>
                        </div>
                    </div>
                </b-tab>
                <b-tab title="All Tournaments">
                    <!-- Display all tournaments -->
                    <div v-for="tournament in allTournaments" :key="tournament.id" class="tournament-item">
                        <h3>{{ tournament.name }}</h3>
                        <p>{{ tournament.venue }} | {{ tournament.dateTime }}</p>
                        <div class="action-buttons">
                            <router-link :to="`/tournament/${tournament.id}`" class="btn btn-primary">View</router-link>
                            <!-- Show Edit and Cancel buttons if the tournament belongs to the organizer -->
                            <template v-if="tournament.organizerId === organizerId">
                                <button class="btn btn-secondary" @click="editTournament(tournament.id)">Edit</button>
                                <button class="btn btn-danger" @click="cancelTournament(tournament.id)">Cancel</button>
                            </template>
                        </div>
                    </div>
                </b-tab>
            </b-tabs>
        </div>
        <div v-else>
            <!-- For players and other roles, display all tournaments -->
            <div v-for="tournament in allTournaments" :key="tournament.id" class="tournament-item">
                <h3>{{ tournament.name }}</h3>
                <p>{{ tournament.venue }} | {{ tournament.dateTime }}</p>
                <div class="action-buttons">
                    <router-link :to="`/tournament/${tournament.id}`" class="btn btn-primary">View</router-link>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import axios from 'axios';
import Navbar from '@/components/NavbarComponent.vue';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            allTournaments: [],
            myTournaments: [],
            userId: 'n7T2VSiOZ2m0kuzEh9yJ', // Hardcoded player ID
            organizerId: 'anQ1ep6A96Wh5oNhidaJ', // Hardcoded organizer ID
            activeTab: 0,
        };
    },
    computed: {
        isOrganizer() {
            return this.organizerId === 'anQ1ep6A96Wh5oNhidaJ';
        },
    },
    mounted() {
        this.fetchTournaments();
    },
    methods: {
        async fetchTournaments() {
            try {
                const response = await axios.get('http://localhost:8080/tournament');
                this.allTournaments = response.data;
                if (this.isOrganizer) {
                    this.myTournaments = this.allTournaments.filter(
                        (tournament) => tournament.organizerId === this.organizerId
                    );
                }
            } catch (error) {
                console.error('Error fetching tournaments:', error);
            }
        },
        editTournament(tournamentId) {
            this.$router.push(`/tournament/${tournamentId}/edit`);
        },
        async cancelTournament(tournamentId) {
            if (confirm('Are you sure you want to cancel this tournament?')) {
                try {
                    await axios.put(`http://localhost:8080/tournament/${tournamentId}/${this.organizerId}`, {
                        status: 'Cancelled',
                    });
                    alert('Tournament cancelled successfully.');
                    this.fetchTournaments(); // Refresh the tournaments list
                } catch (error) {
                    console.error('Error cancelling tournament:', error);
                    alert('Failed to cancel the tournament.');
                }
            }
        },
    },
};
</script>
<style scoped>
.tournaments-page {
    padding: 20px;
}

.tournament-item {
    background-color: #ebe3d5;
    padding: 15px;
    margin-bottom: 15px;
    border-radius: 8px;
}

.tournament-item h3 {
    margin: 0;
}

.action-buttons {
    margin-top: 10px;
}

.action-buttons .btn {
    margin-right: 10px;
}
</style>