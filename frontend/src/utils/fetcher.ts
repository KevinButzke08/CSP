type Options = {
  method: string;
  headers: HeadersInit
  body?: string
}

export async function fetcher(url : string, method : string, body : any) {
  const options : Options = {
    method: method,
    headers: {
      "Content-Type": "application/json",
    },
  };

  if (body) {
    options.body = JSON.stringify(body);
  }

  const response = await fetch(url, options);

  let json;
  try {
    json = await response.json();
  } catch (e) {
    json = null;
  }

  if (!response.ok) {
    const msg = json?.message || json?.error || response.statusText || 'An error occurred.';
    throw new Error(`HTTP ${response.status} ${msg}`);
  }

  return json;
}
