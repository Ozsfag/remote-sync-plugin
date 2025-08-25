package org.blacksoil.remotesync.ui.welcomepage;

import static org.blacksoil.remotesync.ui.welcomepage.WelcomeConstants.buildWelcomeShownKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intellij.openapi.project.Project;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.blacksoil.remotesync.ui.welcomepage.api.WelcomeFacade;
import org.blacksoil.remotesync.ui.welcomepage.listener.WelcomeProjectListener;
import org.junit.jupiter.api.Test;

class WelcomeProjectListenerTest {

  @Test
  void opensWelcomeAndSetsFlagOnFirstRun() {
    Project project = mock(Project.class);
    when(project.isDisposed()).thenReturn(false);

    AtomicBoolean opened = new AtomicBoolean(false);
    AtomicReference<String> savedKey = new AtomicReference<>();
    AtomicReference<Boolean> savedVal = new AtomicReference<>();

    WelcomeFacade fake =
        new WelcomeFacade() {
          boolean shown = false;

          @Override
          public String getPluginVersionOrNull() {
            return "1.2.3";
          }

          @Override
          public boolean isShown(Project p, String key) {
            return shown;
          }

          @Override
          public void setShown(Project p, String key, boolean value) {
            shown = value;
            savedKey.set(key);
            savedVal.set(value);
          }

          @Override
          public void runWhenSmart(Project p, Runnable r) {
            r.run();
          }

          @Override
          public void openWelcome(Project p) {
            opened.set(true);
          }
        };

    new WelcomeProjectListener(fake).projectOpened(project);

    assertTrue(opened.get(), "WelcomeVirtualFile должен открыться");
    assertEquals("remoteSync.welcome.shown.1.2.3", savedKey.get());
    assertEquals(Boolean.TRUE, savedVal.get());
  }

  @Test
  void doesNothingWhenAlreadyShownForVersion() {
    Project project = mock(Project.class);
    when(project.isDisposed()).thenReturn(false);

    AtomicBoolean opened = new AtomicBoolean(false);
    AtomicBoolean flagTouched = new AtomicBoolean(false);
    AtomicBoolean ranSmart = new AtomicBoolean(false);

    WelcomeFacade fake =
        new WelcomeFacade() {
          @Override
          public String getPluginVersionOrNull() {
            return "7.7.7";
          }

          @Override
          public boolean isShown(Project p, String key) {
            return true;
          } // уже показан

          @Override
          public void setShown(Project p, String key, boolean value) {
            flagTouched.set(true);
          }

          @Override
          public void runWhenSmart(Project p, Runnable r) {
            ranSmart.set(true);
          }

          @Override
          public void openWelcome(Project p) {
            opened.set(true);
          }
        };

    new WelcomeProjectListener(fake).projectOpened(project);

    assertFalse(ranSmart.get(), "runWhenSmart не должен вызываться");
    assertFalse(opened.get(), "окно не должно открываться повторно");
    assertFalse(flagTouched.get(), "флаг повторно не выставляется");
  }

  @Test
  void doesNothingWhenPluginNotFound() {
    Project project = mock(Project.class);
    AtomicBoolean anythingCalled = new AtomicBoolean(false);

    WelcomeFacade fake =
        new WelcomeFacade() {
          @Override
          public String getPluginVersionOrNull() {
            return null;
          } // плагин не найден

          @Override
          public boolean isShown(Project p, String key) {
            anythingCalled.set(true);
            return false;
          }

          @Override
          public void setShown(Project p, String key, boolean value) {
            anythingCalled.set(true);
          }

          @Override
          public void runWhenSmart(Project p, Runnable r) {
            anythingCalled.set(true);
          }

          @Override
          public void openWelcome(Project p) {
            anythingCalled.set(true);
          }
        };

    new WelcomeProjectListener(fake).projectOpened(project);

    assertFalse(anythingCalled.get(), "никакие действия не должны выполняться");
  }

  @Test
  void doesNotOpenIfProjectDisposedWhenSmartRuns() {
    Project project = mock(Project.class);
    when(project.isDisposed()).thenReturn(true);

    AtomicBoolean opened = new AtomicBoolean(false);
    AtomicBoolean flagTouched = new AtomicBoolean(false);

    WelcomeFacade fake =
        new WelcomeFacade() {
          @Override
          public String getPluginVersionOrNull() {
            return "2.0.0";
          }

          @Override
          public boolean isShown(Project p, String key) {
            return false;
          }

          @Override
          public void setShown(Project p, String key, boolean value) {
            flagTouched.set(true);
          }

          @Override
          public void runWhenSmart(Project p, Runnable r) {
            r.run();
          } // сразу выполняем

          @Override
          public void openWelcome(Project p) {
            opened.set(true);
          }
        };

    new WelcomeProjectListener(fake).projectOpened(project);

    assertFalse(opened.get(), "не должен открываться для disposed-проекта");
    assertFalse(flagTouched.get(), "флаг не должен ставиться");
  }

  @Test
  void buildKeyFormatsCorrectly() {
    assertEquals("remoteSync.welcome.shown.3.4.5", buildWelcomeShownKey("3.4.5"));
  }
}
