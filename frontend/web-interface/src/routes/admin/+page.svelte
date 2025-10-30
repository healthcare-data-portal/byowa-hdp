<script>
    import AdminView from '$lib/pages/adminView.svelte';
    import { onMount } from 'svelte';
    import { getRoleFromToken, pathForRole } from '$lib/auth';
    import { goto } from '$app/navigation';

    onMount(() => {
        const token = localStorage.getItem('token');
        const role = token ? getRoleFromToken(token) : null;
        if (role !== 'ADMIN') goto(pathForRole(role));
    });
</script>

<AdminView />
