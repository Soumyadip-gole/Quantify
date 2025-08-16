import React, { createContext, useMemo, useState } from 'react';

export const AuthContext = createContext({ user: null, login: () => {}, logout: () => {} });

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  const value = useMemo(() => ({
    user,
    login: (u) => setUser(u ?? { id: '1', name: 'Demo User' }),
    logout: () => setUser(null),
  }), [user]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

