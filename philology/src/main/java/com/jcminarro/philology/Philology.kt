package com.jcminarro.philology

import android.content.Context
import android.content.ContextWrapper
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.jcminarro.philology.transformer.NoneViewTransformer
import com.jcminarro.philology.transformer.SupportToolbarViewTransformer
import com.jcminarro.philology.transformer.TextViewTransformer
import com.jcminarro.philology.transformer.ToolbarViewTransformer
import java.util.Locale

object Philology {
    private val repositoryMap = mutableMapOf<Locale, PhilologyRepository>()
    private var factory: PhilologyRepositoryFactory = object : PhilologyRepositoryFactory{
        override fun getPhilologyRepository(locale: Locale): PhilologyRepository? = null
    }
    private var viewTransformerFactory: ViewTransformerFactory = emptyViewTransformerFactory

    @JvmOverloads
    fun init(factory: PhilologyRepositoryFactory,
             viewTransformerFactory: ViewTransformerFactory = emptyViewTransformerFactory) {
        this.factory = factory
        this.viewTransformerFactory = viewTransformerFactory
        repositoryMap.clear()
    }

    fun wrap(baseContext: Context): ContextWrapper = PhilologyContextWrapper(baseContext)

    internal fun getPhilologyRepository(locale: Locale): PhilologyRepository =
            repositoryMap[locale] ?:
            factory.getPhilologyRepository(locale)?.also {repositoryMap[locale] = it} ?:
                    emptyPhilologyRepository

    internal fun getViewTransformer(view: View): ViewTransformer =
            viewTransformerFactory.getViewTransformer(view) ?:
            internalViewTransformerFactory.getViewTransformer(view)
}

interface PhilologyRepositoryFactory {
    fun getPhilologyRepository(locale: Locale): PhilologyRepository?
}

interface ViewTransformerFactory {
    fun getViewTransformer(view: View): ViewTransformer?
}

private val emptyPhilologyRepository = object : PhilologyRepository{
    override fun getText(key: String): CharSequence? = null
}

private val emptyViewTransformerFactory = object : ViewTransformerFactory {
    override fun getViewTransformer(view: View): ViewTransformer? = null
}

@SuppressWarnings("NewApi")
private val internalViewTransformerFactory = object : ViewTransformerFactory{
    override fun getViewTransformer(view: View): ViewTransformer = when (view) {
        is Toolbar -> SupportToolbarViewTransformer
        is android.widget.Toolbar -> ToolbarViewTransformer
        is TextView -> TextViewTransformer
        else -> NoneViewTransformer
    }
}