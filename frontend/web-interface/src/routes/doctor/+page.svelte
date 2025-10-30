<script>
    import DoctorView from '$lib/pages/doctorView.svelte';
    import { onMount } from 'svelte';
    import { getRoleFromToken, pathForRole } from '$lib/auth';
    import { goto } from '$app/navigation';

    onMount(() => {
        const token = localStorage.getItem('token');
        const role = token ? getRoleFromToken(token) : null;
        if (role !== 'DOCTOR' && role !== 'ADMIN') goto(pathForRole(role));
    });
</script>

<DoctorView />
