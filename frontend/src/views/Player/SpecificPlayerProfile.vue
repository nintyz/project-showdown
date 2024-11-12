<!-- src/views/SpecificPlayerProfile.vue -->
<template>
    <div>
        <PlayerProfile :userId="userId" />
    </div>
</template>

<script>
import PlayerProfile from '@/components/PlayerProfileComponent.vue';

export default {
    components: {
        PlayerProfile,
    },
    computed: {
        userId() {
            return this.$route.params.userId;
        },
    },
};
</script>
