<script>
    import Register from '$lib/pages/Register.svelte';
    import { goto } from '$app/navigation';

    async function handleSubmit(e) {
        const { email, password } = e.detail;

        try {
            const r = await fetch('http://localhost:8080/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: email, password })
            });

            if (r.ok) {
                await goto('/login');
                return;
            }

            let msg = 'Registration failed';
            try {
                const data = await r.json();
                msg = data?.message || msg;
            } catch {  }

            alert(msg);
        } catch {
            alert('Network error');
        }
    }
</script>

<Register on:submit={handleSubmit} on:cancel={() => goto('/login')} />
