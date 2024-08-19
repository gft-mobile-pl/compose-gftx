# Compose GFTX

A set of @Composable methods that accelerate working with Compose.<br />
This library is usually required by other GFT libraries.

## Common
- **Modifier.modifyIf**
  ```kotlin
  @Composable
  inline fun Modifier.modifyIf(
    condition: Boolean,
    crossinline transformation: @Composable Modifier.() -> Modifier,
  ): Modifier
  ``` 

## Data
- **StateFlow\<T\>.toState**
  ```kotlin
  @Composable
  fun <T> StateFlow<T>.toState(
    minActiveState: Lifecycle.State,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
  ): State<T>
  ```

## Interaction
- **InteractionFilter**
  ```kotlin
  @Composable
  fun InteractionFilter(
    minActiveState: Lifecycle.State,
    content: @Composable () -> Unit,
  )
  ```
  ```kotlin
  @Composable
  fun InteractionFilter(
    interactionEnabled: Boolean,
    content: @Composable () -> Unit,
  )
  ```  
- **LifecycleAwareBackHandler**
  ```kotlin
  @Composable
  fun LifecycleAwareBackHandler(
    enabled: Boolean = true,
    minActiveStateForEventsPropagation: Lifecycle.State = Lifecycle.State.RESUMED,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBack: () -> Unit,
  )
  ```
- **DisableTextToolbar**
  ```kotlin
  @Composable
  fun DisableTextToolbar(
    content: @Composable () -> Unit,
  )
  ```
- **Modifier.clearFocusOnClick**
  ```kotlin
  fun Modifier.clearFocusOnClick(): Modifier
  ```
