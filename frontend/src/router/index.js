import { createRouter, createWebHistory } from 'vue-router'

import LandingPage from '@/views/LandingPage.vue'
import LoginPage from '@/views/LoginPage.vue'
import DashboardPage from '@/views/DashboardPage.vue'
import PlayerProfile from '@/views/PlayerProfile.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import AddTournament from '@/views/AddTournament.vue'
import SignUp from '@/views/SignUp.vue'
import DashboardPageTwo from '@/views/DashboardPageTwo.vue'
import AddUserDetails from '@/views/AddUserDetails.vue'
import AllTournaments from '@/views/AllTournaments.vue'
import OAuthCallback from "@/views/OAuthCallback.vue";
import Verify from "@/views/VerifyAccount.vue";
import Verify2FA from "@/views/Verify2FA.vue";

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
        path: '/player-profile',
        name: 'PlayerProfile',
        component: PlayerProfile,
    },
    {
        path: '/admin-dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
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
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
