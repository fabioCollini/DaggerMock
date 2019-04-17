package it.cosenonjaviste.daggermock.builder

import com.nhaarman.mockitokotlin2.mock
import it.cosenonjaviste.daggermock.DaggerMockRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class ComponentDependencyTest {

    @get:Rule
    val rule = DaggerMockRule<MyComponent>(MyComponent::class.java, MyModule())
            .addComponentDependency(MyComponent2::class.java, MyModule2())
            .set { component -> mainService = component.mainService() }

    private val myService: MyService = mock()
    private val myService2: MyService2 = mock()

    private lateinit var mainService: MainService

    @Test
    fun testComponentDependencyModulesCanBeOverriden() {
        assertThat(mainService.myService).isSameAs(myService)
        assertThat(mainService.myService2).isSameAs(myService2)
    }
}
