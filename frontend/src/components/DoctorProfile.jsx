import React, { useEffect, useState } from "react";

export default function DoctorProfile({ providerId }) {
  const [provider, setProvider] = useState(null);

  useEffect(() => {
    if (!providerId) return;
    fetch(`/api/providers/${providerId}`)
      .then((r) => { if (!r.ok) throw new Error("fetch failed"); return r.json(); })
      .then(setProvider)
      .catch((e) => console.error("Could not load provider", e));
  }, [providerId]);

  if (!provider) return <div>Loading...</div>;

  return (
    <div style={{display: "flex", alignItems: "center", gap: 12}}>
      <div style={{
        width:48, height:48, borderRadius:24, background:"#eee", display:"flex",
        alignItems:"center", justifyContent:"center", fontWeight:700
      }}>{provider.initials || "?"}</div>
      <div>
        <div style={{fontWeight:700}}>{provider.providerName}</div>
        <div style={{color:"#666"}}>{provider.providerSourceValue || ""}</div>
      </div>
    </div>
  );
}