const form = document.getElementById("plotForm");
    const button = document.getElementById("generateButton");
    const status = document.getElementById("status");
    const graph = document.getElementById("graph");
    const placeholder = document.getElementById("placeholder");

    let currentImageUrl = null;

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        status.textContent = "";
        status.className = "";
        button.disabled = true;
        button.textContent = "Generating...";

        try {
            const rawCoefficients =
                document.getElementById("coefficients").value;

            const coefficients = rawCoefficients
                .split(",")
                .map(value => value.trim())
                .map(value => {
                    if (value === "") {
                        throw new Error(
                            "Coefficients cannot be empty."
                        );
                    }

                    const number = Number(value);

                    if (!Number.isFinite(number)) {
                        throw new Error(
                            "All coefficients must be finite numbers."
                        );
                    }

                    return number;
                });

            const xMin = Number(
                document.getElementById("xMin").value
            );

            const xMax = Number(
                document.getElementById("xMax").value
            );

            const width = Number(
                document.getElementById("width").value
            );

            const height = Number(
                document.getElementById("height").value
            );

            if (
                !Number.isFinite(xMin) ||
                !Number.isFinite(xMax) ||
                xMin >= xMax
            ) {
                throw new Error(
                    "Minimum X must be less than maximum X."
                );
            }

            if (
                !Number.isInteger(width) ||
                !Number.isInteger(height) ||
                width < 100 || width > 2000 ||
                height < 100 || height > 2000
            ) {
                throw new Error(
                    "Image dimensions must be integers between 100 and 2000."
                );
            }

            const requestBody = {
                coefficients: coefficients,
                xMin: xMin,
                xMax: xMax,
                width: width,
                height: height
            };

            const response = await fetch("/api/plots", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "image/svg+xml"
                },
                body: JSON.stringify(requestBody)
            });

            if (!response.ok) {
                const errorBody = await response.text();

                throw new Error(
                    `API error (${response.status}): ` +
                    (errorBody || "Could not generate the graph.")
                );
            }

            const svgText = await response.text();

            if (
                !svgText.trim().startsWith("<svg") &&
                !svgText.trim().startsWith("<?xml")
            ) {
                throw new Error(
                    "The API response does not appear to be an SVG image."
                );
            }

            const svgBlob = new Blob(
                [svgText],
                { type: "image/svg+xml" }
            );

            const newImageUrl = URL.createObjectURL(svgBlob);

            const previousImageUrl = currentImageUrl;
            currentImageUrl = newImageUrl;

            graph.onload = () => {
                if (previousImageUrl) {
                    URL.revokeObjectURL(previousImageUrl);
                }
            };

            graph.onerror = () => {
                URL.revokeObjectURL(newImageUrl);
                currentImageUrl = null;

                status.textContent =
                    "The SVG was received, but the browser could not display it.";
                status.className = "error";
            };

            graph.src = newImageUrl;
            graph.style.display = "block";
            placeholder.style.display = "none";

            status.textContent = "Graph generated successfully!";
            status.className = "success";

        } catch (error) {
            status.textContent = error.message;
            status.className = "error";
        } finally {
            button.disabled = false;
            button.textContent = "Generate graph";
        }
    });
