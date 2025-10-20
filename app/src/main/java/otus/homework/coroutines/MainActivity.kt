package otus.homework.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val viewModel: CatsViewModel by viewModels {
        val catsService = diContainer.service
        val catsServicePics = diContainer.serviceCatsPics
            CatsViewModelFactory(catsService, catsServicePics)
    }

    private val diContainer = DiContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

        viewModel.attachView(view)
        viewModel.onInitComplete()

        findViewById<Button>(R.id.button).setOnClickListener {
            viewModel.onInitComplete()
        }
    }

    override fun onStop() {
        super.onStop()
    }
}