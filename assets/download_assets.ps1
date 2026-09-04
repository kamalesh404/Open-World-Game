$dirs = @(
    "Models/Player",
    "Models/NPC",
    "Models/Vehicles",
    "Models/Props",
    "Scenes/City",
    "Textures",
    "Audio/SFX",
    "Audio/Ambient"
)

$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Path

foreach ($dir in $dirs) {
    $fullPath = Join-Path -Path $baseDir -ChildPath $dir
    if (!(Test-Path $fullPath)) {
        New-Item -ItemType Directory -Force -Path $fullPath | Out-Null
    }
    Set-Content -Path (Join-Path -Path $fullPath -ChildPath "placeholder.txt") -Value "Place assets for $dir here."
}

Write-Host "Download instructions:"
Write-Host "1. Quaternius Universal Base Characters: https://quaternius.com/packs/universalbasecharacters.html - download GLTF/GLB and place in Models/Player"
Write-Host "2. Quaternius Animation Library: https://quaternius.com/packs/animationlibrary.html - animations for models"
Write-Host "3. Kenney Car Kit: https://kenney.nl/assets/car-kit - download GLTF and place in Models/Vehicles"
Write-Host "4. Quaternius Downtown City MegaKit: https://quaternius.com/packs/downtowncitymegakit.html - place in Scenes/City"
Write-Host "5. Kenney City Kit Roads: https://kenney.nl/assets/city-kit-roads - place road models in Scenes/City"
Write-Host "6. Kenney Nature Kit: https://kenney.nl/assets/nature-kit - place props in Models/Props"
Write-Host "7. Quaternius Modular Men: https://quaternius.com/packs/modularmen.html - download GLTF/GLB and place in Models/NPC"
Write-Host ""
Write-Host "See manifest.json and sources.md for expected filenames and paths."
