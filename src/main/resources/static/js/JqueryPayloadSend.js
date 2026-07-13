document.addEventListener('DOMContentLoaded', () => {
    const button = document.getElementById('query-path');

    if (button) {
        button.addEventListener('click', () => {
            // Ambil text langsung dari elemen INPUT, bukan dari BUTTON
            const inputPath = document.getElementById('query-input').value.trim();
            fetchApiData(inputPath);
        });
    }
});

// Fungsi pembantu untuk mewarnai syntax JSON response
function syntaxHighlight(json) {
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        let cls = 'text-[#ffba38]'; // Default Angka (Orange)
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'text-primary'; // Key JSON (Kuning/Primary)
                return `<span class="${cls}">${match.replace(/:$/, '')}</span>:`;
            } else {
                cls = 'text-secondary'; // String JSON (Hijau Neon)
            }
        } else if (/true|false/.test(match)) {
            cls = 'text-[#ffb4ab]'; // Boolean
        } else if (/null/.test(match)) {
            cls = 'text-on-surface-variant'; // Null
        }
        return `<span class="${cls}">${match}</span>`;
    });
}

async function fetchApiData(path) {
    const viewer = document.getElementById('json-viewer');
    viewer.textContent = "Loading tracking telemetry from VPS instance...";
    const cleanPath = path.startsWith('/') ? path.substring(1) : path;

    $.ajax({
        url: 'http://localhost:8080/api/v1/' + cleanPath,
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            const prettyJson = JSON.stringify(response, null, 2);
            viewer.innerHTML = syntaxHighlight(prettyJson);
        },
        error: function(xhr, status, errorThrown) {
            let errorResponse;

            if (xhr.responseJSON) {
                errorResponse = xhr.responseJSON;
            } else if (xhr.responseText) {
                try {
                    errorResponse = JSON.parse(xhr.responseText);
                } catch(e) {
                    errorResponse = {
                        status: xhr.status,
                        error: errorThrown,
                        message: xhr.responseText || "Unknown connection failure."
                    };
                }
            } else {
                errorResponse = {
                    status: xhr.status || 0,
                    error: "Network Error",
                    message: "Failed to connect to the backend VPS node."
                };
            }
            const prettyJson = JSON.stringify(errorResponse, null, 2);
            viewer.innerHTML = syntaxHighlight(prettyJson);
        }
    });
}