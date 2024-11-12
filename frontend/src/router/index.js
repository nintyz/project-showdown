import { createRouter, createWebHistory } from 'vue-router'

import LandingPage from '@/views/LandingPage.vue'
import LoginPage from '@/views/LoginPage.vue'
import DashboardPage from '@/views/DashboardPage.vue'
// import PlayerProfile from '@/views/PlayerProfile.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import AdminOrganizers from '@/views/AdminOrganizers.vue'
import AddTournament from '@/views/AddTournament.vue'
import SignUp from '@/views/SignUp.vue'
import DashboardPageTwo from '@/views/DashboardPageTwo.vue'
import AddUserDetails from '@/views/Player/AddUserDetails.vue'
import AllTournaments from '@/views/AllTournaments.vue'
import TournamentDetails from '@/views/TournamentDetails.vue'
import TournamentDashboard from '@/views/TournamentDashboard.vue'
import EditTournament from '@/views/EditTournament.vue'
import OAuthCallback from "@/views/OAuthCallback.vue";
import Verify from "@/views/VerifyAccount.vue";
import Verify2FA from "@/views/Verify2FA.vue";
import EditProfile from '@/views/UpdatePlayer.vue'
import AllPlayers from '@/views/AllPlayers.vue'
import PersonalProfile from '@/views/Player/PersonalProfile.vue'
import SpecificPlayerProfile from '@/views/Player/SpecificPlayerProfile.vue'
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
        path: '/dashboard-register',
        name: 'DashboardPageTwo',
        component: DashboardPageTwo,
    },
    {
        path: '/profile',
        name: 'PersonalProfile',
        component: PersonalProfile,
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
        path: '/admin-dashboard',
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
