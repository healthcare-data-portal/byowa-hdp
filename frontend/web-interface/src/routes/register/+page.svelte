<script>
    import { goto } from '$app/navigation';
    import { User, Mail, Lock, Eye, EyeOff } from 'lucide-svelte';

    let name = '';
    let email = '';
    let password = '';
    let confirm = '';
    let showPw = false;
    let showConfirm = false;
    let error = '';

    async function onSubmit(e) {
        e.preventDefault();

        error = '';
        if (!name.trim()) { error = 'Please enter your full name.'; return; }
        if (!/^\S+@\S+\.\S+$/.test(email)) { error = 'Enter a valid email.'; return; }
        if (password.length < 8) { error = 'Password must be at least 8 characters.'; return; }
        if (password !== confirm) { error = 'Passwords do not match.'; return; }

        // Request an Backend senden
        const body = { username: email, password };
        try {
            const res = await fetch('http://localhost:8080/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            const data = await res.json();
            if (res.ok && data.token) {
                localStorage.setItem('token', data.token);
                goto('/app');
            } else {
                error = data.message || 'Registration failed';
            }
        } catch (err) {
            error = 'Network error';
        }
    }

    const backToLogin = () => goto('/');
</script>


<section class="auth-center">
    <div class="auth-card panel">
        <h1 class="auth-title">Create your account</h1>
        <p class="subtitle muted">Join the Healthcare Data Portal</p>

        <form class="form-stack" autocomplete="on" on:submit={onSubmit}>
            <!-- Name -->
            <div class="field">
                <label for="name">Full name</label>
                <div class="input-group">
                    <span class="ix"><User size={18} /></span>
                    <input
                            id="name"
                            class="input has-icon"
                            type="text"
                            bind:value={name}
                            placeholder="Jane Doe"
                            required
                    />
                </div>
            </div>

            <!-- Email -->
            <div class="field">
                <label for="email">Email</label>
                <div class="input-group">
                    <span class="ix"><Mail size={18} /></span>
                    <input
                            id="email"
                            class="input has-icon"
                            type="email"
                            bind:value={email}
                            placeholder="you@example.com"
                            autocomplete="email"
                            required
                    />
                </div>
            </div>

            <!-- Password -->
            <div class="field">
                <label for="password">Password</label>
                <div class="input-group">
                    <span class="ix"><Lock size={18} /></span>
                    <input
                            id="password"
                            class="input has-icon"
                            type={showPw ? 'text' : 'password'}
                            bind:value={password}
                            placeholder="At least 8 characters"
                            autocomplete="new-password"
                            required
                    />
                    <button
                            type="button"
                            class="icon-btn toggle-eye"
                            aria-label={showPw ? 'Hide password' : 'Show password'}
                            on:click|preventDefault={() => (showPw = !showPw)}
                    >
                        {#if showPw}<EyeOff size={18} />{:else}<Eye size={18} />{/if}
                    </button>
                </div>
            </div>

            <!-- Confirm password -->
            <div class="field">
                <label for="confirm">Confirm password</label>
                <div class="input-group">
                    <span class="ix"><Lock size={18} /></span>
                    <input
                            id="confirm"
                            class="input has-icon"
                            type={showConfirm ? 'text' : 'password'}
                            bind:value={confirm}
                            placeholder="Repeat your password"
                            autocomplete="new-password"
                            required
                    />
                    <button
                            type="button"
                            class="icon-btn toggle-eye"
                            aria-label={showConfirm ? 'Hide password' : 'Show password'}
                            on:click|preventDefault={() => (showConfirm = !showConfirm)}
                    >
                        {#if showConfirm}<EyeOff size={18} />{:else}<Eye size={18} />{/if}
                    </button>
                </div>
            </div>

            {#if error}
                <div class="form-error" role="alert">{error}</div>
            {/if}

            <button class="btn primary block btn-lg" type="submit">Create account</button>

            <div class="divider"></div>

            <button class="btn outline block" type="button" on:click={backToLogin}>
                Back to Sign In
            </button>
        </form>
    </div>
</section>
