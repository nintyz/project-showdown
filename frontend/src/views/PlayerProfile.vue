<template>
    <!-- Navbar -->
    <Navbar />
    <div class="profile-container container-fluid pt-4">
        <!-- Player Profile Header -->
        <div class="profile-header row align-items-center">
            <div class="player-image col-md-3 text-center">
                <img src="@/assets/player-image.jpg" alt="Tennis Player" class="player-photo" />
            </div>
            <div class="player-info col-md-9">
                <h1 class="player-name">{{ player.name }}</h1>
                <img class="flag" :src="player.flag" alt="Country Flag" />
                <h3>#{{ player.rank }}</h3>
                <p class="player-bio" v-if="player.bio">
                    {{ player.bio }}
                </p>
                <div class="action-buttons">
                    <button class="btn btn-secondary" @click="updateProfile">Update Biography</button>
                </div>
            </div>
        </div>

        <!-- Player Statistics -->
        <div class="player-stats row text-center mt-4">
            <div class="stat col-md-3">
                <label>Date of Birth</label>
                <p>{{ player.dob }}</p>
            </div>
            <div class="stat col-md-3">
                <label>Rank</label>
                <p>{{ player.rank }}</p>
            </div>
            <div class="stat col-md-3">
                <label>Age</label>
                <p>{{ player.age }}</p>
            </div>
            <div class="stat col-md-3">
                <label>ELO</label>
                <p>{{ player.elo }}</p>
            </div>
        </div>

        <!-- Acknowledge Tournament Section -->
        <div class="acknowledge-section card mt-4">
            <h3 class="card-header">Acknowledge Your Tournament Matches</h3>
            <div class="card-body">
                <h4 class="tournament-name">{{ match.tournament.name }}</h4>
                <p><strong>Date:</strong> {{ match.tournament.date }}</p>
                <p><strong>Location:</strong> {{ match.tournament.location }}</p>
                <div class="match-info row align-items-center mt-4">
                    <div class="col-md-3 text-center">
                        <img src="@/assets/player-image.jpg" alt="Player 1" class="player-photo" />
                        <p>{{ match.player1.name }}</p>
                    </div>
                    <div class="match-stats col-md-3">
                        <p><strong>Rank:</strong> {{ match.player1.rank }}</p>
                        <p><strong>Age:</strong> {{ match.player1.age }}</p>
                        <p><strong>ELO:</strong> {{ match.player1.elo }}</p>
                    </div>

                    <div class="col-md-3 text-center">
                        <img src="https://media.cnn.com/api/v1/images/stellar/prod/gettyimages-2163072777-copy.jpg?c=16x9&q=h_833,w_1480,c_fill"
                            alt="Player 2" class="player-photo" />
                        <p>{{ match.player2.name }}</p>
                    </div>
                    <div class="match-stats col-md-3">
                        <p><strong>Rank:</strong> {{ match.player2.rank }}</p>
                        <p><strong>Age:</strong> {{ match.player2.age }}</p>
                        <p><strong>ELO:</strong> {{ match.player2.elo }}</p>
                    </div>
                </div>
                <button class="acknowledge-btn btn btn-primary mt-3" @click="acknowledgeMatch">Acknowledge</button>
            </div>
        </div>

        <!-- Upcoming Tournaments -->
        <div class="tournaments-section card mt-4">
            <h3 class="card-header">Your Upcoming Tournaments</h3>
            <div class="tournament-list mt-4">
                <li v-for="(tournament, index) in upcomingTournaments" :key="index"
                    class="row align-items-center mb-3 mx-2">
                    <div class="col-md-2">
                        <img :src="tournament.logo" alt="Tournament Logo" class="img-tournament" />
                    </div>
                    <div class="col-md-8">
                        <div class="tournament-info">
                            <h4>{{ tournament.name }}</h4>
                            <p>{{ tournament.location }} | {{ tournament.date }}</p>
                        </div>
                    </div>
                    <div class="col-md-2 text-right">
                        <button class="btn btn-secondary" @click="viewTournament(tournament)">View</button>
                    </div>
                </li>
            </div>
        </div>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import '@/assets/main.css'; 
export default {
    components: {
        Navbar,
    },
    data() {
        return {
            player: {
                name: "Rafael Nadal",
                dob: "03/06/1986",
                rank: 2,
                age: 36,
                elo: 2100,
                flag: "https://flagcdn.com/es.svg", // Example flag URL for Spain
                bio: "Rafael Nadal Parera is a Spanish professional tennis player. He is ranked world No. 2 in singles by the ATP. Known as the 'King of Clay,' Nadal has won numerous Grand Slam titles.",
            },
            upcomingTournaments: [
                {
                    name: "Wimbledon",
                    location: "London, UK",
                    date: "1 - 14 July 2024",
                    logo: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg7iZb7c9r9kR4Quz_H0M9bgyeYyaPvJxJ7_hANon_A2V0EqjG1PmkuXowyF_ums6icjk&usqp=CAU",
                },
                {
                    name: "US Open",
                    location: "New York, USA",
                    date: "26 August - 8 September 2024",
                    logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Usopen-horizontal-logo.svg/1200px-Usopen-horizontal-logo.svg.png",
                },
            ],
            match: {
                tournament: {
                    name: "French Open",
                    location: "Paris, France",
                    date: "7 June 2024",
                },
                player1: {
                    name: "Rafael Nadal",
                    rank: 2,
                    age: 36,
                    elo: 2100,
                    peakElo: 2200,
                },
                player2: {
                    name: "Novak Djokovic",
                    rank: 3,
                    age: 33,
                    elo: 2050,
                    peakElo: 2100,
                },
            },
        };
    },
    methods: {
        updateProfile() {
            alert("Profile update functionality will go here.");
        },
        acknowledgeMatch() {
            alert("You have acknowledged the match!");
        },
        viewTournament(tournament) {
            alert(`Redirecting to the dashboard for ${tournament.name}`);
        },
    },
};
</script>

<style scoped>
/* Main container styling */
.profile-container {
    background-color: #f3eeea;
    padding: 30px;
    font-family: 'Arial', sans-serif;
    margin-left: 0; /* Remove margin */
    margin-right: 0; /* Remove margin */
}

/* Profile header */
.profile-header {
    background-color: #b0a695;
    color: white;
    padding: 30px;
    border-radius: 10px;
}

.player-photo {
    width: 200px;
    height: 200px;
    object-fit: cover;
    border-radius: 50%;
    border: 5px solid white;
}

.player-name {
    font-size: 36px;
    margin-bottom: 5px;
}

.flag {
    width: 40px;
    margin: 10px 0;
}

.player-bio {
    font-size: 16px;
    margin: 10px 0;
}

/* Action button */
.action-buttons {
    margin-top: 10px;
}

/* Player stats */
.player-stats .stat label {
    font-weight: bold;
    font-size: 16px;
    color: #776b5d;
}

.player-stats .stat p {
    background-color: #fff;
    padding: 15px;
    border-radius: 8px;
    font-size: 18px;
    color: #776b5d;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* Acknowledge Section */
.acknowledge-section {
    background-color: #ebe3d5;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.acknowledge-section h4 {
    color: #776b5d;
    margin-bottom: 10px;
}

.acknowledge-section p {
    color: #776b5d;
}

.acknowledge-section img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 50%;
}

.match-stats p {
    margin: 5px 0;
    font-size: 14px;
    color: #776b5d;
}

/* Button styles */
.acknowledge-btn {
    background-color: #776b5d;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
}

.acknowledge-btn:hover {
    background-color: #b0a695;
}

/* Card header */
.card-header {
    background: #776b5d;
    color: white;
}

/* Upcoming Tournaments Section */
.tournaments-section {
    background-color: #ebe3d5;
    padding: 20px;
    border-radius: 10px;
    margin-top: 30px;
}

.tournament-list li {
    display: flex;
    align-items: center;
    background-color: #fff;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.img-tournament {
    width: 100px;
    object-fit: cover;
    border-radius: 10px;
}

.tournament-info h4 {
    font-size: 18px;
    color: #776b5d;
}

.tournament-info p {
    font-size: 14px;
    color: #776b5d;
}

/* Button styling */
.btn-secondary {
    background-color: #776b5d;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
}

.btn-secondary:hover {
    background-color: #b0a695;
}
</style>
