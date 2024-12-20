import { createRouter, createWebHistory } from 'vue-router'

import LandingPage from '@/views/LandingPage.vue'
import LoginPage from '@/views/LoginPage.vue'
import DashboardPage from '@/views/DashboardPage.vue'
// import PlayerProfile from '@/views/PlayerProfile.vue'
import OrganizerProfile from '@/views/OrganizerProfile.vue'
import AdminDashboard from '@/views/AllTournamentsDashboard.vue'
import AdminOrganizers from '@/views/AdminOrganizers.vue'
import AddTournament from '@/views/AddTournament.vue'
import SignUp from '@/views/SignUp.vue'
import AddUserDetails from '@/views/AddUserDetails.vue'
import AllTournaments from '@/views/AllTournaments.vue'
import TournamentDetails from '@/views/TournamentDetails.vue'
import TournamentDashboard from '@/views/TournamentDashboard.vue'
import EditTournament from '@/views/EditTournament.vue'
import OAuthCallback from "@/views/OAuthCallback.vue";
import Verify from "@/views/VerifyAccount.vue";
import Verify2FA from "@/views/Verify2FA.vue";
import EditProfile from '@/views/UpdatePlayer.vue'
import EditOrganizer from '@/views/UpdateOrganizer.vue'
import AllPlayers from '@/views/AllPlayers.vue'
import PersonalProfile from '@/views/PersonalProfile.vue'
import SpecificPlayerProfile from '@/views/SpecificPlayerProfile.vue'
import MatchUpdate from '@/views/MatchUpdate.vue'

const routes = [
    {
        path: '/',
        name: 'LandingPage',
        component: LandingPage,
    },
    {
        path: '/login',
        name: 'LoginPage',
        component: LoginPage,
    },
    {
        path: '/signup',
        name: 'SignUp',
        component: SignUp,
    },
    {
        path: '/user-details',
        name: 'AddUserDetails',
        component: AddUserDetails,
    },
    {
        path: '/dashboard',
        name: 'DashboardPage',
        component: DashboardPage,
    },
    {
        path: '/profile/player/',
        name: 'PersonalProfile',
        component: PersonalProfile,
    },
    {
        path: '/profile/personalOrganizer/',
        name: 'OrganizerProfile',
        component: OrganizerProfile,
    },
    {
        path: '/player/:userId',
        name: 'SpecificPlayerProfile',
        component: SpecificPlayerProfile,
        props: true,
    },

    {
        path: '/edit-profile',
        name: 'EditProfile',
        component: EditProfile,
    },
    {
        path: '/edit-organizer',
        name: 'EditOrganizer',
        component: EditOrganizer,
    },
    {
        path: '/all-tournaments-dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
    },
    {
        path: '/admin-organizers',
        name: 'AdminOrganizers',
        component: AdminOrganizers,
    },
    {
        path: '/new-tournament',
        name: 'AddTournament',
        component: AddTournament,
    },
    {
        path: '/all-tournaments',
        name: 'AllTournaments',
        component: AllTournaments,
    },
    {
        path: '/tournament/:id',
        component: TournamentDetails,
    },
    {
        path: '/tournament/:id/dashboard',
        component: TournamentDashboard,
    },
    {
        path: '/tournament/:id/edit',
        component: EditTournament,
    },
    {
        path: '/oauth2/callback',
        name: 'OAuthCallback',
        component: OAuthCallback,
    },
    {
        path: '/verify',
        name: 'Verify',
        component: Verify,
    },
    {
        path: '/verify-2fa',
        name: 'Verify2FA',
        component: Verify2FA,
    },
    {
        path: '/all-players',
        name: 'AllPlayers',
        component: AllPlayers,
    },
    {
        path: '/match-management/:id',
        name: 'MatchUpdate',
        component: MatchUpdate,
    },

]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
