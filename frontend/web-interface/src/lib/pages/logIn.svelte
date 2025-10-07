<script>
    import { createEventDispatcher } from 'svelte';
    import { Eye, EyeOff, Mail, Lock } from 'lucide-svelte';
    import { goto } from '$app/navigation';

    const dispatch = createEventDispatcher();
    let email = '';
    let password = '';
    let showPassword = false;

    function submitLogin() {
        dispatch('login', { email, password });
    }
    function onSubmit(e) {
        e.preventDefault();
        submitLogin();
    }

    const toggleShow = () => (showPassword = !showPassword);
    const register   = () => goto('/register');
</script>

<section class="auth-center">
    <div class="auth-card panel">
        <h1 class="auth-title">Healthcare Data Portal</h1>
        <p class="subtitle muted">FHIR → OMOP Data Management System</p>

        <form on:submit={onSubmit} class="form-stack" autocomplete="on">
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
                            placeholder="Enter your email"
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
                            type={showPassword ? 'text' : 'password'}
                            bind:value={password}
                            placeholder="Enter your password"
                            autocomplete="current-password"
                            required
                    />
                    <button type="button" class="icon-btn" on:click={toggleShow}>
                        {#if showPassword}
                            <EyeOff size={18} />
                        {:else}
                            <Eye size={18} />
                        {/if}
                    </button>
                </div>
            </div>

            <button class="btn primary block btn-lg" type="submit">Log In</button>
            <div class="divider"></div>
            <button class="btn outline block" type="button" on:click={register}>
                Create Account
            </button>
        </form>
    </div>
</section>
