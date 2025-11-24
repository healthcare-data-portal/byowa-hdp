<script>
    import PatientView from '$lib/pages/patientView.svelte';
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';

    let personalInfo = null;

    onMount(async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            goto('/login');
            return;
        }

        await fetchPersonalInfo(token);
    });

    async function fetchPersonalInfo(token) {
        try {
            const res = await fetch('http://localhost:8080/api/personal-info', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            let data = null;
            try { data = await res.json(); } catch {}

            if (res.ok && data) {
                personalInfo = data;
            } else {
                const msg = typeof data === 'string' ? data : (data?.message || 'Failed to load personal information');
                alert(msg);
            }
        } catch {
            alert('Network error');
        }
    }
</script>

<PatientView {personalInfo} />
