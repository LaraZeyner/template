package net.mmm.mc.template.util.objectmanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import net.mmm.mc.template.util.Messenger;

/**
 * Created by Lara on 30.07.2019 for template
 */

public final class ObjectBuilder {
  /**
   * Wandle ein Objekt in einen String um
   *
   * @param object zu serialisierendes Objekt
   * @return serialisierter String
   */
  public static String getStringOf(final Serializable object) {
    if (object != null) {
      try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
           final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
        objectOutputStream.writeObject(object);
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(outputStream.toByteArray());
      } catch (final IOException ex) {
        Messenger.administratorMessage(ex.getMessage());
      }
    }
    return null;
  }

  /**
   * Wandle einen serialisierten String in ein Objekt um
   *
   * @param basic serialisierter String
   * @return Objekt
   */
  public static Object getObjectOf(final String basic) {
    final Base64.Decoder decoder = Base64.getDecoder();
    final byte[] data = decoder.decode(basic);
    try (final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
         final ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream)) {
      return objectInputStream.readObject();
    } catch (final IOException ex) {
      Messenger.administratorMessage(ex.getMessage());
    } catch (final ClassNotFoundException ignored) {
      Messenger.administratorMessage("Klasse konnte nicht gefunden werden.");
    } catch (final ClassCastException ignored) {
      Messenger.administratorMessage("Zielobjekt muss Serialize implementieren.");
    }
    return null;
  }
}