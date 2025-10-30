<script>
    import PatientView from '$lib/pages/patientView.svelte';
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';

    onMount(() => {
        const token = localStorage.getItem('token');
        if (!token) goto('/login');
    });
</script>

<PatientView />
