export type Role = 'ADMIN' | 'DOCTOR' | 'PATIENT' | null;

function b64urlDecode(s: string): string {
	s = s.replace(/-/g, '+').replace(/_/g, '/');
	const pad = s.length % 4 ? 4 - (s.length % 4) : 0;
	const base64 = s + '='.repeat(pad);

	const a = (globalThis as any).atob as ((b64: string) => string) | undefined;
	if (!a) return '';

	const bin = a(base64);
	return decodeURIComponent(
		Array.from(bin, c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0')).join('')
	);
}

export function getRoleFromToken(token: string): Role {
	try {
		const payloadRaw = token.split('.')[1] ?? '';
		const json = b64urlDecode(payloadRaw);
		if (!json) return null;
		const payload = JSON.parse(json);
		const raw = (payload.role ?? payload.roles ?? '').toString().toUpperCase();
		const norm = raw.replace(/^ROLE_/, '');
		return (['ADMIN', 'DOCTOR', 'PATIENT'] as const).includes(norm as any)
			? (norm as Role)
			: null;
	} catch {
		return null;
	}
}

export function pathForRole(r: Role) {
	if (r === 'ADMIN') return '/admin';
	if (r === 'DOCTOR') return '/doctor';
	if (r === 'PATIENT') return '/patient';
	return '/login';
}
