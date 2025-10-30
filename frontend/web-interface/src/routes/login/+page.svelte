<script>
    import Login from '$lib/pages/Login.svelte';
    import { getRoleFromToken, pathForRole } from '$lib/auth';
    import { goto } from '$app/navigation';

    async function handleLogin(e) {
        const { email, password } = e.detail;

        try {
            const res = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: email, password })
            });

            let data = null;
            try { data = await res.json(); } catch {}

            if (res.ok && data?.token) {
                localStorage.setItem('token', data.token);
                const role = getRoleFromToken(data.token);
                await goto(pathForRole(role));
            } else {
                const msg = data?.message || 'Login failed';
                alert(msg);
            }
        } catch {
            alert('Network error');
        }
    }
</script>

<Login on:login={handleLogin} />
