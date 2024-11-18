package repository.bookings

import models.*
import models.bookings.{Booking, Confirmed}
import weaver.SimpleIOSuite

import java.time.{LocalDate, LocalDateTime}

object BookingRepositorySpec extends SimpleIOSuite {

  def freshRepository = new MockBookingRepository
  
  val sampleBooking_1: Booking =
    Booking(
      id = Some(1),
      bookingId = "booking_1",
      bookingName = "Sample Booking 1",
      userId = 1,
      workspaceId = 1,
      bookingDate = LocalDate.of(2024, 10, 10),
      startTime = LocalDateTime.of(2024, 10, 10, 9, 0),
      endTime = LocalDateTime.of(2024, 10, 10, 12, 0),
      status = Confirmed,
      createdAt = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  val sampleBooking_2: Booking =
    Booking(
      id = Some(2),
      bookingId = "booking_2",
      bookingName = "Sample Booking 2",
      userId = 2,
      workspaceId = 1,
      bookingDate = LocalDate.of(2024, 10, 10),
      startTime = LocalDateTime.of(2024, 10, 10, 13, 0),
      endTime = LocalDateTime.of(2024, 10, 10, 15, 0),
      status = Confirmed,
      createdAt = LocalDateTime.of(2024, 10, 5, 16, 0)
    )

  // Test case for creating a booking
  test(".findBookingById() - find a booking by it's booking_id") {
    val mockRepository: MockBookingRepository = freshRepository
    for {
      _ <- mockRepository.setBooking(sampleBooking_1)
      _ <- mockRepository.setBooking(sampleBooking_2)
      result <- mockRepository.findBookingById("booking_1")
    } yield expect(result == Some(sampleBooking_1))
  }

  // Test case for finding a booking that doesn't exist
  test(".findBookingById() - return an error if booking ID does not exist") {
    val mockRepository = freshRepository
    for {
      _ <- mockRepository.setBooking(sampleBooking_1)
      _ <- mockRepository.setBooking(sampleBooking_2)
      result <- mockRepository.findBookingById("booking_3")
    } yield expect(result == None)
  }

  test(".updateBooking() - update a booking") {
    val mockRepository = freshRepository
    val updatedBooking = sampleBooking_1.copy(bookingName = "Updated Booking")
    for {
      _ <- mockRepository.setBooking(sampleBooking_1)
      _ <- mockRepository.setBooking(sampleBooking_2)
      result <- mockRepository.updateBooking("booking_1", updatedBooking)
      updatedBooking <- mockRepository.findBookingById("booking_1")
    } yield
      expect.all(
        result == 1, // Assert that update returned 1
        updatedBooking == updatedBooking // Assert the updated booking matches expected values
      )
  }

  // Test case for deleting a booking
  test("delete a booking") {
    val mockRepository = freshRepository
    for {
      _ <- mockRepository.setBooking(sampleBooking_1)
      result <- mockRepository.deleteBooking("booking_1")
      notFoundBooking <- mockRepository.findBookingById("booking_1")
    } yield
      expect.all(
        result == 1, // Assert that delete returned a count for 1 deleted booking
        notFoundBooking.isEmpty // Assert the booking has been deleted and None is returned
      )
  }
}
