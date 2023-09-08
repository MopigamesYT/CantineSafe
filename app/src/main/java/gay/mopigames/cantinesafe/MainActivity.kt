package gay.mopigames.cantinesafe

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import gay.mopigames.cantinesafe.ui.theme.CantineSafeTheme

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private lateinit var gyroscopeSensor: Sensor
    private lateinit var accelerometerSensor: Sensor
    private lateinit var gyroscopeListener: SensorEventListener
    private lateinit var accelerometerListener: SensorEventListener

    // Seuil pour déclencher l'affichage de l'image (à adapter selon tes besoins)
    private val seuilGyroscope = 9.0f // Réduit la sensibilité au gyroscope

    // Seuil pour déterminer si l'appareil est posé à plat (à adapter selon tes besoins)
    private val seuilInclinaison = 15.0f // Réduit la marge pour considérer l'appareil comme posé à plat

    // État pour contrôler l'affichage de l'image
    private var showImage by mutableStateOf(false)

    // Délai en millisecondes avant de masquer l'image
    private val delaiAvantMasquage = 2000L // Augmente le délai à 2 secondes

    private var lastTimeImageShown = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CantineSafeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (showImage) {
                        // Affiche l'image ici si showImage est vrai
                        Image(
                            painter = painterResource(R.drawable.qr), // Remplace 'qr' par le nom de ton image sans l'extension
                            contentDescription = "Image Gyroscope",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Affiche le texte par défaut
                        Greeting("Mopigames")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        gyroscopeListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                    val gyroscopeValues = event.values
                    // Utilise les valeurs du gyroscope pour déterminer si l'image doit être affichée
                    if (conditionPourAfficherImage(gyroscopeValues)) {
                        showImage = true
                        lastTimeImageShown = System.currentTimeMillis()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Gère les changements de précision si nécessaire
            }
        }

        accelerometerListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    val acceleration = event.values
                    // Vérifie si l'appareil est posé à plat
                    if (estPoseAplat(acceleration) && System.currentTimeMillis() - lastTimeImageShown >= delaiAvantMasquage) {
                        showImage = true
                        masquerImageAvecDelai() // Masque l'image avec un délai
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Gère les changements de précision si nécessaire
            }
        }

        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // Arrête l'écoute des capteurs lorsque l'activité est en pause
        sensorManager.unregisterListener(gyroscopeListener)
        sensorManager.unregisterListener(accelerometerListener)
    }

    // Fonction pour déterminer si l'appareil est posé à plat
    private fun estPoseAplat(acceleration: FloatArray): Boolean {
        // Calcul de l'angle d'inclinaison par rapport à l'horizontale
        val angleInclinaison = Math.toDegrees(Math.acos(acceleration[2].toDouble() / SensorManager.GRAVITY_EARTH))
        // Vérifie si l'angle d'inclinaison est inférieur au seuil
        return angleInclinaison < seuilInclinaison
    }

    // Fonction pour masquer l'image après un délai
    private fun masquerImageAvecDelai() {
        // Utilise Handler pour retarder la modification de l'état showImage
        Handler().postDelayed({
            showImage = false
        }, delaiAvantMasquage)
    }

    // Fonction pour déterminer si l'image doit être affichée en fonction des valeurs du gyroscope
    private fun conditionPourAfficherImage(gyroscopeValues: FloatArray): Boolean {
        // Implémente ta propre logique pour décider quand afficher l'image en fonction des valeurs du gyroscope
        // Par exemple, tu pourrais comparer gyroscopeValues avec un seuil
        return false // Remplace ceci par ta propre logique
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // ...
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CantineSafeTheme {
        Greeting("Mopigames")
    }
}

