function syntaxHighlight(json) {
            json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
            return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
                let cls = 'text-[#ffba38]';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'text-primary';
                        return `<span class="${cls}">${match.replace(/:$/, '')}</span>:`;
                    } else {
                        cls = 'text-secondary';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'text-[#ffb4ab]';
                } else if (/null/.test(match)) {
                    cls = 'text-on-surface-variant';
                }
                return `<span class="${cls}">${match}</span>`;
            });
        }

        document.addEventListener('DOMContentLoaded', () => {
            document.querySelectorAll('.auto-json').forEach(async (block) => {
                const url = block.getAttribute('data-src');

                // HANYA proses jika ada data-src (berarti kita mau ambil file luar)
                if (url) {
                    try {
                        const response = await fetch(url);
                        if (!response.ok) throw new Error(`HTTP ${response.status}`);

                        const jsonObj = await response.json();
                        const prettyJson = JSON.stringify(jsonObj, null, 2);
                        block.innerHTML = syntaxHighlight(prettyJson);
                    } catch (error) {
                        block.innerHTML = `<span class="text-error">Gagal memuat JSON dari ${url}: ${error.message}</span>`;
                    }
                } else {
                    // Kalau nggak ada data-src, berarti itu JSON hardcoded di dalam HTML
                    try {
                        const rawText = block.textContent.trim();
                        if (rawText) {
                            const jsonObj = JSON.parse(rawText);
                            block.innerHTML = syntaxHighlight(JSON.stringify(jsonObj, null, 2));
                        }
                    } catch (e) {
                        console.warn("Melewati parsing untuk elemen ini (bukan JSON):", block);
                    }
                }
            });
        });